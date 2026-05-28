package com.hotel.hotel_server.dto;

import com.hotel.hotel_server.domain.Hotel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotelCreateResponse {
    private Long hotelId;

    public static HotelCreateResponse from(Hotel hotel){
        return HotelCreateResponse.builder()
                .hotelId(hotel.getId())
                .build();
    }
}
