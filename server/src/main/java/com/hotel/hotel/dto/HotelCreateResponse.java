package com.hotel.hotel.dto;

import com.hotel.hotel.domain.Hotel;
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
