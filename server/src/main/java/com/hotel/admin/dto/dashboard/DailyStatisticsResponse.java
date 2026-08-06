package com.hotel.admin.dto.dashboard;


import java.time.LocalDate;

public record DailyStatisticsResponse (
        LocalDate date,
        long reservationCount,
        long paymentCount,
        long paymentTotal
){}
