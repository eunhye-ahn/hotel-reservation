package com.hotel.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record TossWebhookRequest (
     String eventType,
     String createdAt,
     WebhookData data
){
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WebhookData (
         String paymentKey,
         String orderId,
         String status
    ){}
}
