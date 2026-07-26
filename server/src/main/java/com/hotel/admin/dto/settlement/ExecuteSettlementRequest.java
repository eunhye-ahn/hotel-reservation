package com.hotel.admin.dto.settlement;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExecuteSettlementRequest {
    private LocalDate periodStart;
    private LocalDate periodEnd;
}
