package com.hotel.admin.controller;

import com.hotel.admin.dto.payment.AdminSettlementSearchResponse;
import com.hotel.admin.dto.settlement.ExecuteSettlementRequest;
import com.hotel.admin.service.AdminSettlementService;
import com.hotel.payment.domain.SettlementSearchType;
import com.hotel.payment.domain.SettlementSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/settlement")
@RequiredArgsConstructor
public class AdminSettlementController {
    private final AdminSettlementService adminSettlementService;

    //관리자 수동 정산
    @PostMapping("/{hotelId}/execute")
    public ResponseEntity<Void> executeSettlementByAdmin(@PathVariable Long hotelId,
                                                         @RequestBody ExecuteSettlementRequest request){
        adminSettlementService.executeSettlementByAdmin(hotelId, request.getPeriodStart(), request.getPeriodEnd());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //전체 호텔 정산내역 조회
    @GetMapping
public ResponseEntity<Page<AdminSettlementSearchResponse>> getSettlements(@RequestParam(required = false) SettlementSearchType searchType,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam(required = false)Boolean hasPendingBalance,
                                                                              @RequestParam(required = false) SettlementSortType sortType,
                                                                              @RequestParam(required = false,defaultValue = "0")int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<AdminSettlementSearchResponse> result = adminSettlementService.getSettlements(searchType, keyword, hasPendingBalance,sortType, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //특정 호텔 기간별 정산액 조회
    @GetMapping("{hotelId}/preview")
    public ResponseEntity<Integer> previewSettlementAmount(@PathVariable Long hotelId,
                                                           @RequestParam(required = false) LocalDate periodStart,
                                                           @RequestParam(required = false) LocalDate periodEnd){
        int amount = adminSettlementService.previewSettlementAmount(hotelId,  periodStart, periodEnd);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(amount);
    }
}
