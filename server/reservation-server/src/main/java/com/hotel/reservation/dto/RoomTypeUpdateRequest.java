package com.hotel.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomTypeUpdateRequest {
    private String roomTypeName;
    private Integer maxOccupancy;
    private String imageUrl;
}
