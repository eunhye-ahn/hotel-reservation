package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Hotel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotelUpdateResponse {
    private Long hotelId;

    public static HotelUpdateResponse from(Hotel hotel){
        return HotelUpdateResponse.builder()
                .hotelId(hotel.getId())
                .build();
    }
}
