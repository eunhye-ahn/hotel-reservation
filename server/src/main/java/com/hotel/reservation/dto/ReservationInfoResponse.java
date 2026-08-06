package com.hotel.reservation.dto;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.RoomTypeInventory;
import java.util.List;


public record ReservationInfoResponse (
     int availableCount,      // 잔여객실
     int totalPrice
){
    public static ReservationInfoResponse from(List<RoomTypeInventory> inventories, int totalPrice) {
        //기간 합산 내 잔여객실 조회
        int availableCount = inventories.stream()
                .mapToInt(RoomTypeInventory::getAvailableCount)
                .min()
                .orElseThrow(()->new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND));

        return new ReservationInfoResponse(
                availableCount,
                totalPrice
        );
    }
}
