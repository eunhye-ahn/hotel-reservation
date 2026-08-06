package com.hotel.admin.dto.inventory;

import com.hotel.hotel.domain.RoomTypeInventorySortType;

import java.time.LocalDate;

public record InventorySearchRequest (
        LocalDate date,
        String hotelName,
        RoomTypeInventorySortType sortType,
        int page
){}
