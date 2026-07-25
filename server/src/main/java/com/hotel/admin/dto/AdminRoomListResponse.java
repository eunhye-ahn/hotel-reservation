package com.hotel.admin.dto;

import com.hotel.hotel.domain.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminRoomListResponse {
    private Long id;
    private String roomTypeName;
    private String roomName;
    private int roomNumber;
    private int floor;
    private boolean roomStatus;
    private boolean available;

    public static AdminRoomListResponse from(Room room) {
        return AdminRoomListResponse.builder()
                .id(room.getId())
                .roomTypeName(room.getRoomType().getName())
                .roomName(room.getName())
                .roomNumber(room.getNumber())
                .floor(room.getFloor())
                .roomStatus(room.isUsable())
                .build();
    }
}
