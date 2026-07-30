package com.hotel.admin.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeInventoryCalendarResponse {
    private Long roomTypeId;
    private String roomTypeName;
    private List<InventoryCalendarCellResponse> cells;  //7일치
}
