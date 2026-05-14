package com.hotel.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class HotelUpdateRequest {
    private String hotelName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
}