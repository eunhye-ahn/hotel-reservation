package com.hotel.admin.controller;

import com.hotel.admin.dto.payment.AdminPaymentResponse;
import com.hotel.admin.service.AdminPaymentService;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.domain.PaymentSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/payment")
public class AdminPaymentController {
    private final AdminPaymentService adminPaymentService;

    //결제내역 조회
    @GetMapping
    public ResponseEntity<Page<AdminPaymentResponse>> getPayments(@RequestParam(required = false) PaymentSearchType searchType,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) LocalDate startDate,
                                                                  @RequestParam(required = false) LocalDate endDate,
                                                                  @RequestParam(required = false) PaymentOrderStatus status,
                                                                  @RequestParam(required = false, defaultValue = "0")int page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<AdminPaymentResponse> result = adminPaymentService.getPayments(searchType, keyword, startDate, endDate, status, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
