package com.hotel.reservation.domain;

public enum ReservationStatus {
    PENDING_PAYMENT,    //결제대기(10분)
    BEFORE_USE,    // 이용전 (결제완료, 확정)
    AFTER_ISE,      //이용후
    CANCELED,      // 고객취소
    EXPIRED         //만료 (결제시간만료)
}
