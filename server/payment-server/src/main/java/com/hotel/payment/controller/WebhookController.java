package com.hotel.payment.controller;

import com.hotel.payment.dto.TossWebhookRequest;
import com.hotel.payment.dto.TossWebhookResponse;
import com.hotel.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [WHAT] toss payments 웹훅 수신 컨트롤러
 * [WHY] 토스가 결제 완료 후 비동기로 알림을 보내면 수신하여 처리
 *
 * [흐름]
 * 토스 -> POST /api/v1/payments/webhook
 * -> 이벤트 타입 확인 (PAYMENT_STATUS_CHANGED)
 * -> 결제 상태 확인 (DONE/CANCELED/ABORTED/EXPIRED/PARTIAL_CANCELED)
 * -> 상태에 따라 처리
 *
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class WebhookController {
    private  final WebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<TossWebhookResponse> handleWebhook(@RequestBody TossWebhookRequest request){
        TossWebhookResponse result = webhookService.handleWebhook(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
