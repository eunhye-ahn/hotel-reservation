package com.hotel.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
public class TossWebhookRequest {
    private String eventType;
    private String createdAt;
    private WebhookData data;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WebhookData {
        private String paymentKey;
        private String orderId;
        private String status;
    }
}
