package com.hotel.admin.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminSettlementSearchResponse {
    private Long hotelId;
    private String sellerAccount;
    private String hotelName;
    private int pendingBalance;
    private LocalDateTime lastSettledAt;
    private int totalSettlementAmount;
}
