package com.hotel.admin.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCalendarCellResponse {
    private Long inventoryId;
    private LocalDate date;
    private int totalInventory;
    private int totalReserved;
    private int availableCount;
}
