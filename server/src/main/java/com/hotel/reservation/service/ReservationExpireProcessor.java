package com.hotel.reservation.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationExpireProcessor {
    private final ReservationRepository reservationRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void expireReservation(String reservationKey){
        Reservation reservation = reservationRepository.findByReservationKey(reservationKey)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.getPaymentStatus() != PaymentStatus.PENDING){
            return;
        }

        reservation.updateReservationExpire();

        List<RoomTypeInventory> inventories = roomTypeInventoryRepository.findByRoomTypeIdAndDateBetween(
                reservation.getRoomType().getId(),
                reservation.getStartDate(),reservation.getEndDate().minusDays(1));

        for(RoomTypeInventory inventory : inventories){
            inventory.restore(reservation.getNumberOfRooms());
        }

    }

    @Recover
    public void expireRecover(RuntimeException e, String reservationKey){
        log.error("예약 만료 처리 재시도 모두 실패 - reservationKey: {}", reservationKey);
    }
}
