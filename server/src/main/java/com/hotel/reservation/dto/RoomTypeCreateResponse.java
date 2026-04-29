package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Room;
import com.hotel.reservation.domain.RoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomTypeCreateResponse {
    private Long roomTypeId;

    public static RoomTypeCreateResponse from(RoomType roomType){
        return RoomTypeCreateResponse.builder().
                roomTypeId(roomType.getId()).build();
    }
}
