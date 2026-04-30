package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomInfoResponse {
    private Long roomId;
    private String roomName;
    private int floor;
    private int number;
    private boolean usable;

    public static RoomInfoResponse from(Room room){
        return RoomInfoResponse.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .floor(room.getFloor())
                .number(room.getNumber())
                .usable(room.isUsable())
                .build();
    }
}
