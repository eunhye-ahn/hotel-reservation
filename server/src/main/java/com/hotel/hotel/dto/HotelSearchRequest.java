package com.hotel.hotel.dto;

import java.time.LocalDate;

public record HotelSearchRequest (
    String q,
    String lDongRegnCd,
    String lDongSignguCd,
    String lclsSystm2,
    LocalDate startDate,
    LocalDate endDate,
    Integer numberOfGuests,
    Integer numberOfRooms,
    int page
) {
    public HotelSearchRequest {
        if(numberOfRooms == null ){
            numberOfRooms = 1;
        }
    }
}
