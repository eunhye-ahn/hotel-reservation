package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Rate;
import com.hotel.reservation.domain.RoomType;
import com.hotel.reservation.domain.RoomTypeInventory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomTypeResponse {
    private Long roomTypeId;
    private String name;
    private int maxOccupancy;
    private String imageUrl;
    private int maxRate;
    private int demandRate;
    private int discountRate;
    private int availableCount; //잔여객실

    //여러테이블의 칼럼을 모으다보니
    //변환로직을 한곳에서 유지보수하기위해 정적팩토리메서드로 생성
    public static RoomTypeResponse from(RoomType roomType, Rate rate, RoomTypeInventory inventory){
        int discountRate = (int) Math.round(
                (double)(rate.getMaxRate() - rate.getDemandRate()) / rate.getMaxRate() * 100
        );

        return RoomTypeResponse.builder()
                .roomTypeId(roomType.getId())
                .name(roomType.getName())
                .maxOccupancy(roomType.getMaxOccupancy())
                .imageUrl(roomType.getImageUrl())
                .maxRate(rate.getMaxRate())
                .demandRate(rate.getDemandRate())
                .discountRate(discountRate)
                .availableCount(inventory.getAvailableCount())
                .build();
    }
}
