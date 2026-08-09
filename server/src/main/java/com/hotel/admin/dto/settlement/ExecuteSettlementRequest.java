package com.hotel.admin.dto.settlement;


import java.time.LocalDate;

public record ExecuteSettlementRequest (
        String settlementKey,
        LocalDate periodStart,
        LocalDate periodEnd
){}
