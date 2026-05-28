package com.hotel.payment.controller;

import com.hotel.payment.dto.*;
import com.hotel.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare/{reservationKey}")
    public ResponseEntity<PaymentPrepareResponse> preparedPayment(
            @PathVariable String reservationKey,
            @RequestBody PaymentPrepareRequest request,
            HttpServletRequest httpRequest
    ){
        PaymentPrepareResponse result = paymentService.preparePayment(reservationKey,request.getOrderId(), httpRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

//    @GetMapping("/{orderId}/reservationKey")
//    public ResponseEntity<String> getReservationKey(@PathVariable String orderId){
//        String reservationKey = paymentService.getReservationKey(orderId);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(reservationKey);
//    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(@RequestBody PaymentConfirmRequest request){
        PaymentConfirmResponse result = paymentService.confirmPayment(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

}
