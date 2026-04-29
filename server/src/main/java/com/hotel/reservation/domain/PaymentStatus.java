package com.hotel.reservation.domain;

public enum PaymentStatus {
    PENDING,                //결제대기
    PAID,                   //결제완료
    REFUNDED,               //환불완료
    CANCELED,               //취소
    REJECTED                //승인실패
}
