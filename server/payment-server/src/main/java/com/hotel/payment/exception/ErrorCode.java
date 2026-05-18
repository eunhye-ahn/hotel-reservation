package com.hotel.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 결제입니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 주문을 찾을 수 없습니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String message){
        this.status = status;
        this.message = message;
    }
}
