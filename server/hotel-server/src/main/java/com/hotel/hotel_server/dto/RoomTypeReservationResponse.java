package com.hotel.hotel_server.dto;

import com.hotel.hotel_server.domain.RoomTypeInventory;
import com.hotel.hotel_server.exception.CustomException;
import com.hotel.hotel_server.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomTypeReservationResponse {
    private int availableCount;      // 잔여객실
    private int demandRate;          // 현재가
    private int totalPrice;

    public static RoomTypeReservationResponse from(List<RoomTypeInventory> inventories, int totalDemandRate, int totalPrice) {
        //기간 합산 내 잔여객실 조회
        int availableCount = inventories.stream()
                .mapToInt(RoomTypeInventory::getAvailableCount)
                .min()
                .orElseThrow(()->new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND));

        return RoomTypeReservationResponse.builder()
                .availableCount(availableCount)
                .demandRate(totalDemandRate)
                .totalPrice(totalPrice)
                .build();
    }
}
