package com.hotel.hotel.dto;

import com.hotel.hotel.domain.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomCreateResponse {
    private Long roomId;

    public static RoomCreateResponse from(Room room){
        return RoomCreateResponse.builder().
                roomId(room.getId()).build();
    }
}
