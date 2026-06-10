package com.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TossWebhookResponse {
    private final String reservationKey;
}
