package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Room;
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
