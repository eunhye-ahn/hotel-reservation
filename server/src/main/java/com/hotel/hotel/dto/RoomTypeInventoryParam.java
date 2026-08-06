package com.hotel.hotel.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public record RoomTypeInventoryParam (
        Long hotelId,
        LocalDate today,
        LocalDate startDate,
        LocalDate endDate,
        Integer totalDays,
        Integer numberOfRooms,
        Integer numberOfGuests
){ }