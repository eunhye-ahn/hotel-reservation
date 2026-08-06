package com.hotel.hotel.dto;


import com.hotel.hotel.domain.Hotel;

import java.time.LocalDate;
import java.util.List;

public record HotelSearchParam (
     List<Long> hotelIds,
     String lDongRegnCd,
     String lDongSignguCd,
     String lclsSystm2,
     LocalDate startDate,
     LocalDate endDate,
     Integer numberOfGuests,
     Integer numberOfRooms,
     LocalDate today,
     int totalDays,
     long offset,
     int size
){ }
