package com.hotel.hotel.dto;

import com.hotel.hotel.domain.RoomType;
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
