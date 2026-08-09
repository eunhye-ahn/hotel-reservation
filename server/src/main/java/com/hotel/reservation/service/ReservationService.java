package com.hotel.reservation.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.common.idempotency.IdempotencyDomain;
import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.domain.*;
import com.hotel.reservation.dto.*;
import com.hotel.reservation.repository.*;
import com.hotel.common.idempotency.IdempotencyRedisService;
import com.hotel.reservation.service.process.ReservationProcessor;
import com.hotel.user.domain.User;
import com.hotel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ReservationCreateResponse createReservation(ReservationRequest request, Long userId, String reservationKey) {
        try {
            reservationProcessor.processWithRetry(request, userId, reservationKey);
            idempotencyRedisService.complete(IdempotencyDomain.RESERVATION, reservationKey);
            return new ReservationCreateResponse(
                    reservationKey,
                    reservationRepository.findByReservationKey(reservationKey)
                            .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND))
                            .getOrderId()
            );
        } catch (Exception e) {
            idempotencyRedisService.fail(IdempotencyDomain.RESERVATION, reservationKey);
            throw e;
        }
    }

    //내 예약조회
    public Page<ReservationResponse> getMyReservations(Long userId, ReservationStatus status, Pageable pageable) {
        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //내예약조회
        Page<Reservation> reservations = reservationRepository.findByUserAndReservationStatusOrderByCreatedAtDesc(user, status, pageable);

        return reservations.map(ReservationResponse::from);
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


    //예약상태확인
    public String getReservationStatus(String reservationKey){
        Reservation reservation = reservationRepository.findByReservationKey(reservationKey)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return reservation.getPaymentStatus().name();
    }
}