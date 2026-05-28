package com.hotel.hotel_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomTypeCreateRequest {
    private String roomTypeName;
    private int maxOccupancy;
    private String imageUrl;
}
