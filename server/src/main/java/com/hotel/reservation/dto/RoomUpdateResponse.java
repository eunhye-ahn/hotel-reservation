package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomUpdateResponse {
    private Long roomId;

    public static RoomUpdateResponse from(Room room) {
        return RoomUpdateResponse.builder().
                roomId(room.getId()).build();
    }
}
