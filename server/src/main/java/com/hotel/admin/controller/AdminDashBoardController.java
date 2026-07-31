package com.hotel.admin.controller;

import com.hotel.admin.dto.dashboard.*;
import com.hotel.admin.service.AdminDashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashBoardController {
    private final AdminDashBoardService adminDashBoardService;

    @GetMapping("/summary")
    public ResponseEntity<DashBoardSummaryResponse>  getDashBoardSummaryInfo(){
        DashBoardSummaryResponse result = adminDashBoardService.getDashBoardSummaryInfo();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/dailyStatistics")
    public ResponseEntity<List<DailyStatisticsResponse>>  getDailyStatisticsInfo(){
        List<DailyStatisticsResponse> result = adminDashBoardService.getDailyStatisticsInfo();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/unAssign-reserve")
    public ResponseEntity<List<UnassignRoomInfo>> getUnAssignReservationInfo(){
        List<UnassignRoomInfo> result = adminDashBoardService.unAssignReservationInfo();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/reserve-status/statiscs")
    public ResponseEntity<List<ReserveStatusStaticResponse>> getReserveStatusByMonth(){
        List<ReserveStatusStaticResponse> result = adminDashBoardService.getReserveStatusByMonth();
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/payment-status/statics")
    public ResponseEntity<List<PaymentStatusStaticResponse>> getPaymentStatusByMonth(){
        List<PaymentStatusStaticResponse> result = adminDashBoardService.getPaymentStatusByMonth();
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/top-pending/hotels")
    public ResponseEntity<List<TopPendingBalanceHotel>> getTopPendingHotels(){
        List<TopPendingBalanceHotel> result = adminDashBoardService.getTopPendingHotels();
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
