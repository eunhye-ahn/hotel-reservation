package com.hotel.admin.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminInventorySummaryResponse {
    private Long hotelId;
    private String hotelName;
    private String lDongSignguCd;
    private int roomTypeCount;
    private int totalInventory;
    private int totalReserved;
    private int availableCount;
    private double reserveRate;
}
