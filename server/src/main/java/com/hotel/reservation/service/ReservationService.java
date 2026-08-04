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
import com.hotel.reservation.service.process.IdempotencyRedisService;
import com.hotel.reservation.service.process.ReservationProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    }

    //내 예약조회
    public List<ReservationResponse> getMyReservations(Long userId, ReservationStatus status) {
        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //내예약조회
        List<ReservationResponse> reservations = reservationRepository.findByUserAndReservationStatusOrderByCreatedAtDesc(user, status)
                .stream().map(ReservationResponse::from)
                .toList();

        //오늘 기준

        return reservations;
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
        User user = userRepository.findById(userId)
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
    public void cancelReservationByUser(Long userId, String reservationKey) {

        Reservation reservation = reservationRepository.findByUserIdAndReservationKey(userId, reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        //검증 및 상태변경 BEFORE_USE -> CANCELED
        reservation.cancelByUser();

        List<RoomTypeInventory> inventories = roomTypeInventoryRepository
                .findByRoomTypeIdAndDateBetween(reservation.getRoomType().getId(), reservation.getStartDate(), reservation.getEndDate().minusDays(1));
        //재고복구
        inventories.forEach(i -> i.restore(reservation.getNumberOfRooms()));
    }

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
