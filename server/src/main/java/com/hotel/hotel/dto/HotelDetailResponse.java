package com.hotel.hotel.dto;

import com.hotel.hotel.domain.Hotel;

import java.time.LocalTime;
import java.util.List;

public record HotelDetailResponse (
    Long hotelId,
    String hotelName,
    String address,
    String imageUrl,
    LocalTime checkInTime,
    LocalTime checkOutTime,
    List<RoomTypeResponse> roomTypes
){
    public static HotelDetailResponse from(Hotel hotel, List<RoomTypeResponse> roomTypes){
        return new HotelDetailResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getImageUrl(),
                hotel.getCheckInTime(),
                hotel.getCheckOutTime(),
                roomTypes
        );
    }
}
