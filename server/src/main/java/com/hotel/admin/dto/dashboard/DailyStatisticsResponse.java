package com.hotel.admin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class DailyStatisticsResponse {
    LocalDate date;
    long reservationCount;
    long paymentCount;
    long paymentTotal;
}
