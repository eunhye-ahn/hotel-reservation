package com.hotel.admin.dto.settlement;


import java.time.LocalDate;

public record ExecuteSettlementRequest (
     LocalDate periodStart,
     LocalDate periodEnd
){}
