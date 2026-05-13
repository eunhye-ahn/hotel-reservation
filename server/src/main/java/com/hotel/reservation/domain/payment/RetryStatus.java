package com.hotel.reservation.domain.payment;

public enum RetryStatus {
    PENDING,        //재시도 큐 등록
    PROCESSING,     //재시도 실행중
    SUCCESS,        //재시도 성공 -> 삭제 or 상태변경
    EXHAUSTED       //임계값 초과 -> dead letter queue 이동
}