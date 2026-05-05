package com.hotel.reservation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.reservation.domain.*;
import com.hotel.reservation.dto.IdempotencyValue;
import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.ReservationDetailResponse;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    private final RateRepository rateRepository;
    private final ReservationRepository reservationRepository;
    private final IdempotencyRedisService idempotencyRedisService;

    /**
     *
     * 1. 멱등키 확인 (Redis)
     * -중복요청이면 이전 결과 반환
     * -새요청이면 예약처리진행
     * 2. 예약가능여부확인-DB
     * 3. 예약 생성-DB
     * 4. Redis 결과 업데이트
     *
     * IdempotencyValue {
     *     status      : processing | completed | failed
     *     result      : true/false (null 가능)
     *     userId      : 다른 유저 방지
     *     requestHash : 요청 본문 해시 (변조 감지)
     *     createdAt   : 생성 시간
     * }
     */
    //예약생성
    @Transactional
    public void createReservation(ReservationRequest request, Long userId){
        //멱등키 확인 -Redis
        //1.요청본문 해시생성
        String requestHash = generateHash(request);

        //2.redis선점시도
        boolean isFirst = idempotencyRedisService.tryProcessing(
                request.getReservationKey(), userId, requestHash
        );

        //3.중복요청이면 이전 요청으로 처리
        if(!isFirst){
            handleDuplicate(request.getReservationKey(), userId, requestHash);
            return;
        }

        //4.새요청이면 예약처리(여기서 엔티티유효성검사 등하고 response 반환)
        try{
            process(request, userId);
            idempotencyRedisService.complete(request.getReservationKey());
        }catch(Exception e){
            idempotencyRedisService.fail(request.getReservationKey());
            throw e;
        }
    }

    //내 예약조회
    public List<ReservationResponse> getMyReservations(Long userId){
        //엔티티 조회
        User User = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        //내예약조회
        return reservationRepository.findByUser(User)
                .stream().map(ReservationResponse::from)
                .toList();
    }

    //전체예약조회 -관리자
    public List<ReservationResponse> getReservations(){

        return reservationRepository.findAll()
                .stream().map(ReservationResponse::from)
                .toList();
    }

    //예약취소
    public void deleteReservation(Long userId, Long reservationId){

        Reservation reservation = reservationRepository.findByIdAndUserId(reservationId, userId)
                .orElseThrow(()->new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.cancel();
    }

    /**
     * [WHAT] 요청본문을 해시로 변환 : 변조검지용 방어로직
     *
     * [WHY] 같은 reservationKey로 다른 본문이 오면 감지하기 위해
     *
     * [흐름]
     * request 객체 → JSON 문자열 → SHA-256 해시
     *
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
     *
     * [WHY]
     * tryProcessing()이 false일 때 (이미 redis에 있음)
     * 기존 값을 꺼내서 상황에 맞게 처리
     *
     * [흐름]
     * get()으로 기존 값 조회
     * ├── userId 다름   → 403
     * ├── requestHash 다름 → 422
     * ├── processing   → 409
     * ├── completed    → 200 (이전 result 반환)
     * └── failed       → 500
     */
    private void handleDuplicate(String reservationKey, Long userId, String requestHash){
        //tryProcessing() -> get() 사이에서 멱등키 만료되는 상황 방지
        //      : 현실적으로는 TTL을 24시간으로 설정해두어서 발생할 확률이 없지만 이론상 방어
        IdempotencyValue value = idempotencyRedisService.get(reservationKey)
                .orElseThrow(()-> new CustomException(ErrorCode.IDEMPOTENCY_NOT_FOUND));

        //다른 유저가 같은 키 사용 시도
        if(!value.getUserId().equals(userId)){
            throw new CustomException(ErrorCode.IDEMPOTENCY_USER_MISMATCH);
        }

        //같은 키인데 다른 본문
        if(!value.getRequestHash().equals(requestHash)){
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

    private void process(ReservationRequest request, Long userId){
        //엔티티조회-유효성검사
        Hotel hotel = hotelRepository
                .findById(request.getHotelId())
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //예약날짜 서비스레벨 차단(외 @Valid 애플리케이션레벨 차단 / DB제약조건 DB레벨 차단)
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }


        /**
         * [n+1문제 발생]
         * for문 돌때마다 재고 N번과와 금액 N번 -> 총 db조회 2N번 발생
         */
        int totalPrice = 0;

        for(LocalDate date = request.getStartDate();date.isBefore(request.getEndDate());date=date.plusDays(1)){
            //재고확인 및 차감
            RoomTypeInventory inventory = roomTypeInventoryRepository
                    .findByRoomTypeAndDate(roomType, date)
                    .orElseThrow(()-> new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND));
            inventory.reserve(request.getNumberOfRoomsToReserve());
            //날짜별 금액 합산
            Rate rate = rateRepository.findByRoomTypeAndDate(roomType, date)
                    .orElseThrow(()-> new CustomException(ErrorCode.RATE_NOT_FOUND));
            totalPrice += request.getNumberOfRoomsToReserve()*rate.getDemandRate();
        }

        //예약생성
        Reservation reservation = Reservation.builder()
                .reservationKey(request.getReservationKey())
                .hotel(hotel)
                .roomType(roomType)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfRooms(request.getNumberOfRoomsToReserve())
                .totalPrice(totalPrice)
                .paymentStatus(PaymentStatus.PAID)
                .reservationStatus(ReservationStatus.BEFORE_USE)
                .build();

        reservationRepository.save(reservation);
    }
}
