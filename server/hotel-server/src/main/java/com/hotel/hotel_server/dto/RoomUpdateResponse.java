package com.hotel.hotel_server.dto;

import com.hotel.hotel_server.domain.Room;
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
