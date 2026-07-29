package com.hotel.reservation.domain;

public enum ReservationStatus {
    BEFORE_USE,    // 이용전 (결제완료, 확정)
    AFTER_USE,      //이용후
    CANCELED,      // 고객취소
    EXPIRED,         //만료 (결제시간만료)
    CANCEL_PENDING //환불처리 대기중
}
