package com.hotel.payment.dto;

public record TossConfirmRequest (
     String paymentKey,  //토스가 준 결제 고유키(승인필수)
     String orderId,     //서버 paymentOrderId(서버-PSP 멱등키)
     int amount         //결제금액(위변조 검증)
){}
