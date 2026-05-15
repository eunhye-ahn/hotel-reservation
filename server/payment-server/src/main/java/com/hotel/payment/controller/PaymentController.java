package com.hotel.payment.controller;

import com.hotel.payment.dto.PaymentPrepareResponse;
import com.hotel.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/prepare/{reservationKey}")
    public ResponseEntity<PaymentPrepareResponse> preparedPayment(
            @PathVariable String reservationKey){
        PaymentPrepareResponse result = paymentService.preparePayment(reservationKey);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
