package com.hotel.hotel.dto;

import java.time.LocalDate;

public record RoomTypeSearchParam (
        LocalDate startDate,
        LocalDate endDate,
        Integer numberOfRooms,
        Integer numberOfGuests
){
    public RoomTypeSearchParam {
        if(numberOfRooms == null){
            numberOfRooms = 1;
        }
    }
}
