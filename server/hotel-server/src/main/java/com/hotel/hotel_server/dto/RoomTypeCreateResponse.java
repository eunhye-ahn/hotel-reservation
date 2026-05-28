package com.hotel.hotel_server.dto;

import com.hotel.hotel_server.domain.RoomType;
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
