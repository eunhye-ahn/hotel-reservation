package com.hotel.admin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DashBoardSummaryResponse {
    private int todayCheckInCount;
    private int unassignedCount;
    private long todayPaymentAmount;
    private long totalPendingBalance;
    private int failedPaymentCount;
}
