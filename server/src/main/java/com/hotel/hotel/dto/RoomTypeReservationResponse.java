package com.hotel.hotel.dto;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.RoomTypeInventory;

import java.util.List;


public record RoomTypeReservationResponse(
        //잔여객실
        int availableCount,
        int demandRate,
        int totalPrice
) {

    public static RoomTypeReservationResponse from(List<RoomTypeInventory> inventories, int totalDemandRate, int totalPrice) {
        //기간 합산 내 잔여객실 조회
        int availableCount = inventories.stream()
                .mapToInt(RoomTypeInventory::getAvailableCount)
                .min()
                .orElseThrow(()->new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND));

        return new RoomTypeReservationResponse(
                availableCount,
                totalDemandRate,
                totalPrice
        );
    }
}
