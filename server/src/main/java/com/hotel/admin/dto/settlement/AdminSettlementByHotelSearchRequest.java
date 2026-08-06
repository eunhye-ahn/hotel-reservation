package com.hotel.admin.dto.settlement;

import com.hotel.payment.domain.SettlementStatus;

import java.time.LocalDate;

public record AdminSettlementByHotelSearchRequest(
         Long hotelId,
         LocalDate startDate,
         LocalDate endDate,
         SettlementStatus status,
         int page
) { }
