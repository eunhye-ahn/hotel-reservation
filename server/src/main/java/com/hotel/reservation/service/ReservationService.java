package com.hotel.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.domain.*;
import com.hotel.reservation.dto.*;
import com.hotel.reservation.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final IdempotencyRedisService idempotencyRedisService;
    private final ReservationProcessor reservationProcessor;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    /**
     *
     * 1. 멱등키 확인 (Redis)
     * -중복요청이면 이전 결과 반환
     * -새요청이면 예약처리진행
     * 2. 예약가능여부확인-DB
     * 3. 예약 생성-DB
     * 4. Redis 결과 업데이트
     * <p>
     * IdempotencyValue {
     * status      : processing | completed | failed
     * result      : true/false (null 가능)
     * userId      : 다른 유저 방지
     * requestHash : 요청 본문 해시 (변조 감지)
     * createdAt   : 생성 시간
     * }
     */
    //예약생성
    public ReservationCreateResponse createReservation(ReservationRequest request, Long userId) {
        //멱등키 확인 -Redis
        //1.요청본문 해시생성
        String requestHash = generateHash(request);

        //2.redis선점시도
        boolean isFirst = idempotencyRedisService.tryProcessing(
                request.getReservationKey(), userId, requestHash
        );

        //3.중복요청이면 이전 요청으로 처리
        if (!isFirst) {
            handleDuplicate(request.getReservationKey(), userId, requestHash);
            return new ReservationCreateResponse(
                    request.getReservationKey(),
                    reservationRepository.findByReservationKey(request.getReservationKey())
                            .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND))
                            .getOrderId()
            );
        }

        //4.새요청이면 예약처리(여기서 엔티티유효성검사 등하고 response 반환)
        try {
            reservationProcessor.processWithRetry(request, userId);
            idempotencyRedisService.complete(request.getReservationKey());
            return new ReservationCreateResponse(
                    request.getReservationKey(),
                    reservationRepository.findByReservationKey(request.getReservationKey())
                            .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND))
                            .getOrderId()
            );
        } catch (Exception e) {
            idempotencyRedisService.fail(request.getReservationKey());
            throw e;
        }

        /**
         * [커밋 시점] - 메서드 종료 시
         * @Transactional이 커밋 실행
         * → JPA 더티체킹
         *   → inventory 스냅샷 vs 현재값 비교
         *   → 변경됐으면 UPDATE 자동 실행
         *   → UPDATE ... WHERE id=? AND version=0
         *   → 0 rows updated → OptimisticLockException → 롤백()
         */
    }

    //내 예약조회
    public List<ReservationResponse> getMyReservations(Long userId, ReservationStatus status) {
        //엔티티 조회
        User User = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //내예약조회
        return reservationRepository.findByUserAndReservationStatus(User, status)
                .stream().map(ReservationResponse::from)
                .toList();
    }

    //예약상세조회 -예약확인
    public ReservationDetailResponse reservationConfirm(
            Long userId, String reservationKey
    ) {
        Reservation reservation = reservationRepository.findByUserIdAndReservationKey(userId, reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return ReservationDetailResponse.from(reservation);
    }

    //결제폼 -예약정보확인
    public ReservationInfoResponse getReservationInfo(Long userId,
                                                      String reservationKey){
        //유효성
        User User = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Reservation reservation = reservationRepository.findByReservationKey(reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        //잔여객실조회
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository.findByRoomTypeIdAndDateBetween(reservation.getRoomType().getId(), reservation.getStartDate(),
                reservation.getEndDate().minusDays(1));

        return ReservationInfoResponse.from(inventories, reservation.getTotalPrice());
    }

    //전체예약조회 -관리자
    public List<ReservationResponse> getReservations() {

        return reservationRepository.findAll()
                .stream().map(ReservationResponse::from)
                .toList();
    }

    //예약취소
    @Transactional
    public void deleteReservation(Long userId, String reservationKey) {

        Reservation reservation = reservationRepository.findByUserIdAndReservationKey(userId, reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.cancel();

        //n+1 발생
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository
                .findByRoomTypeIdAndDateBetween(reservation.getRoomType().getId(), reservation.getStartDate(), reservation.getEndDate().minusDays(1));

        inventories.forEach(i -> i.restore(reservation.getNumberOfRooms()));
    }

    /**
     * [WHAT] 요청본문을 해시로 변환 : 변조검지용 방어로직
     * <p>
     * [WHY] 같은 reservationKey로 다른 본문이 오면 감지하기 위해
     * <p>
     * [흐름]
     * request 객체 → JSON 문자열 → SHA-256 해시
     * <p>
     * [비교]
     * JWT - HS256 -> 비밀키 + SHA-256으로 서명 (검증가능)
     * SHA-256 -> 단방향 해시 (복원불가)
     */
    private String generateHash(ReservationRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // LocalDate 직렬화
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            //굳이 custom으로 해야하나 흠
            throw new CustomException(ErrorCode.HASH_GENERATION_FAILED);
        }
    }

    /**
     * [WHAT] 중복요청 처리
     * <p>
     * [WHY]
     * tryProcessing()이 false일 때 (이미 redis에 있음)
     * 기존 값을 꺼내서 상황에 맞게 처리
     * <p>
     * [흐름]
     * get()으로 기존 값 조회
     * ├── userId 다름   → 403
     * ├── requestHash 다름 → 422
     * ├── processing   → 409
     * ├── completed    → 200 (이전 result 반환)
     * └── failed       → 500
     */
    private void handleDuplicate(String reservationKey, Long userId, String requestHash) {
        //tryProcessing() -> get() 사이에서 멱등키 만료되는 상황 방지
        //      : 현실적으로는 TTL을 24시간으로 설정해두어서 발생할 확률이 없지만 이론상 방어
        IdempotencyValue value = idempotencyRedisService.get(reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.IDEMPOTENCY_NOT_FOUND));

        //다른 유저가 같은 키 사용 시도
        if (!value.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_USER_MISMATCH);
        }

        //같은 키인데 다른 본문
        if (!value.getRequestHash().equals(requestHash)) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_REQUEST_MISMATCH);
        }

        if (value.getStatus().equals("processing")) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_PROCESSING);
        }

        if (value.getStatus().equals("failed")) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_FAILED);
        }

        if (!value.getStatus().equals("completed")) {
            throw new CustomException(ErrorCode.IDEMPOTENCY_UNKNOWN);
        }

        //completed면 정상 반환 -> status를 enum으로 관리하면 코드가독성 좋아짐
    }

    //예약서비스 -> 결제서비스에 줄 데이터 담기
    public ReservationFeignResponse getReservationForPayment(String reservationKey) {
        Reservation reservation = reservationRepository.findByReservationKey(reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return ReservationFeignResponse.builder()
                .reservationId(reservation.getId())
                .reservationKey(reservationKey)
                .paymentStatus(reservation.getPaymentStatus().toString())
                .amount(reservation.getTotalPrice())
                .sellerAccount(reservation.getHotel().getSellerAccount())
                .userId(reservation.getUser().getId())
                .build();
    }

    //예약상태확인
    public String getReservationStatus(String reservationKey){
        Reservation reservation = reservationRepository.findByReservationKey(reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return reservation.getPaymentStatus().name();
    }
}
