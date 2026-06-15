package com.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TossConfirmRequest {
    private String paymentKey;  //토스가 준 결제 고유키(승인필수)
    private String orderId;     //서버 paymentOrderId(서버-PSP 멱등키)
    private int amount;         //결제금액(위변조 검증)
}
