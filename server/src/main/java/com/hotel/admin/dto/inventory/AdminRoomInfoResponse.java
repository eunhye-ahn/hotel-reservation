package com.hotel.admin.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminRoomInfoResponse {
    private Long roomId;
    private String roomName;
    private int floor;
    private int roomNumber;
    private String roomTypeName;
    private boolean usable;
}
