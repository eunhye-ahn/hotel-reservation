package com.hotel.payment.domain;

public enum PaymentStatus {
    PENDING,                //결제대기
    PAID,                   //결제완료
    REFUNDED,               //환불완료
    CANCELED,               //취소
    FAILED                  //결제실패
}
