package com.hotel.admin.controller;

import com.hotel.admin.dto.payment.AdminPaymentResponse;
import com.hotel.admin.dto.payment.AdminPaymentSearchRequest;
import com.hotel.admin.service.AdminPaymentService;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.domain.PaymentSearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/payment")
public class AdminPaymentController {
    private final AdminPaymentService adminPaymentService;

    //결제내역 조회
    @GetMapping
    public ResponseEntity<Page<AdminPaymentResponse>> getPayments(@ModelAttribute AdminPaymentSearchRequest request) {
        Pageable pageable = PageRequest.of(request.page(),10);
        Page<AdminPaymentResponse> result = adminPaymentService.getPayments(
                request.searchType(),
                request.keyword(),
                request.startDate(),
                request.endDate(),
                request.status(),
                pageable
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
