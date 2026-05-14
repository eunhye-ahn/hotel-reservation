package com.hotel.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomUpdateRequest {
    private String roomName;
    private Integer floor;
    private Integer number;
    private Boolean usable;
}
