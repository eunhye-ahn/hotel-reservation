package com.hotel.admin.controller;

import com.hotel.admin.dto.dashboard.DashBoardSummaryResponse;
import com.hotel.admin.service.AdminDashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
