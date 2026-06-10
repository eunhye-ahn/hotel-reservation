package com.hotel.hotel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomCreateRequest {
    private String roomName;
    private int floor;
    private int number;
}
