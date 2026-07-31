package com.hotel.admin.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TopPendingBalanceHotel {
    private long hotelId;
    private String hotelName;
    private long pendingBalance;
}
