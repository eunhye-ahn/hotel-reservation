package com.hotel.admin.dto.payment;

import com.hotel.payment.domain.PaymentOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AdminPaymentResponse {
    private String paymentOrderId;
    private String hotelName;
    private String userName;
    private int amount;
    private PaymentOrderStatus status;
    private LocalDateTime createdAt;
}
