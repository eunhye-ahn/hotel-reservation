package com.hotel.hotel_server.dto;

import com.hotel.hotel_server.domain.RoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomTypeUpdateResponse {
    private Long roomTypeId;

    public static RoomTypeUpdateResponse from(RoomType roomType){
        return RoomTypeUpdateResponse.builder().
                roomTypeId(roomType.getId()).build();
    }
}
