package com.hotel.admin.dto;

import com.hotel.hotel.domain.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminRoomResponse {
    private Long id;
    private String roomTypeName;
    private String roomName;
    private int roomNumber;
    private int floor;
    private boolean roomStatus;
    private boolean available;
    private boolean currentlyAssigned;
}
