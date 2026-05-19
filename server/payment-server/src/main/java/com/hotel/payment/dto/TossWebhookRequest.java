package com.hotel.payment.dto;

import lombok.Getter;

@Getter
public class TossWebhookRequest {
    private String eventType;
    private String createdAt;
    private WebhookData data;

    @Getter
    public static class WebhookData {
        private String paymentKey;
        private String orderId;
        private String status;
    }
}
