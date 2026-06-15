package com.hotel.hotel.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RoomTypeInventoryParam {
    private Long hotelId;
    private LocalDate today;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private Integer numberOfRooms;
    private Integer numberOfGuests;
}