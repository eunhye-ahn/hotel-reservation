package com.hotel.hotel.dto;

import com.hotel.hotel.domain.Room;

public record RoomInfoResponse (
        Long roomId,
        String roomName,
        int floor,
        int number,
        boolean usable
){
    public static RoomInfoResponse from(Room room){
        return new RoomInfoResponse(
                room.getId(),
                room.getName(),
                room.getFloor(),
                room.getNumber(),
                room.isUsable()
        );
    }
}
