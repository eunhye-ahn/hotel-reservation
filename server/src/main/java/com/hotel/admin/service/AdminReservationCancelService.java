package com.hotel.admin.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.payment.service.PaymentService;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservationCancelService {
    private final ReservationRepository reservationRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    private final PaymentService paymentService;

    //재고 복구,예약상태변경, 방배정 철회
    @Transactional
    public void cancelAndRestoreInventory(Long reservationId, String reason){


        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.cancelByAdmin(reason);

        //재고복구(중복코드) - 룸타입서비스로 리팩토링 예정
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository
                .findByRoomTypeIdAndDateBetween(reservation.getRoomType().getId(), reservation.getStartDate(), reservation.getEndDate().minusDays(1));

        inventories.forEach(i -> i.restore(reservation.getNumberOfRooms()));
    }

    //취소 정산
    @Transactional
    public void completeCancelByRefund(Long reservationId, String reason){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        paymentService.reverseSettlement(reservation);
        reservation.completeCancelByRefund();
    }
}
