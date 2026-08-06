package com.hotel.admin.controller;


import com.hotel.admin.dto.inventory.*;
import com.hotel.admin.service.AdminInventoryService;
import com.hotel.hotel.domain.RoomTypeInventorySortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {
    private final AdminInventoryService adminInventoryService;
    @GetMapping
    public ResponseEntity<Page<AdminInventorySummaryResponse>> searchInventorySummary(
            @ModelAttribute InventorySearchRequest request){
        Pageable pageable = PageRequest.of(request.page(), 10);

        Page<AdminInventorySummaryResponse> result = adminInventoryService.searchInventorySummary(
                request.date(),
                request.hotelName(),
                request.sortType(),
                pageable
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<List<RoomTypeInventoryCalendarResponse>> getInventoryCalendar(@PathVariable Long hotelId,
                                                                                        @RequestParam LocalDate startDate,
                                                                                        @RequestParam LocalDate endDate){
        List<RoomTypeInventoryCalendarResponse> result = adminInventoryService.getInventoryCalendar(hotelId, startDate, endDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{hotelId}/room")
    public ResponseEntity<Page<AdminRoomInfoResponse>> searchByRoomInfo(@PathVariable Long hotelId, @RequestParam(required = false) Long roomTypeId, @RequestParam(required = false) Integer floor,@RequestParam(required = false,defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<AdminRoomInfoResponse> result = adminInventoryService.searchByRoomInfo(hotelId, roomTypeId, floor, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{hotelId}/room/filter-option")
    public ResponseEntity<RoomFilterOptionResponse> getFilterOptions(@PathVariable Long hotelId){
        RoomFilterOptionResponse result =  adminInventoryService.getFilterOptions(hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
