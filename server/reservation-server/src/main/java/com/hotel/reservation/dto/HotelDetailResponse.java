package com.hotel.reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

//junit
//jackson 라이브러리

@Getter
@Builder
public class HotelDetailResponse {
    private Long hotelId;
    private String hotelName;
    private String address;
    private String imageUrl;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    private List<RoomTypeResponse> roomTypes;
}
