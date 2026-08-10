package com.hotel.admin.service;

import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.domain.Reservation;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservationCancelService {
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    //재고 복구,예약상태변경, 방배정 철회
    @Transactional
    public void cancelAndRestoreInventory(@Nonnull Reservation reservation, String reason){

        reservation.cancelByAdmin(reason);

        //재고복구(중복코드) - 룸타입서비스로 리팩토링 예정
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository
                .findByRoomTypeIdAndDateBetweenOrderByDateAsc(reservation.getRoomType().getId(), reservation.getStartDate(), reservation.getEndDate().minusDays(1));

        inventories.forEach(i -> i.restore(reservation.getNumberOfRooms()));
    }
}
