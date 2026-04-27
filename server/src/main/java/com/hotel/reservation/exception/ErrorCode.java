package com.hotel.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //user
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이메일/비밀번호가 틀렸습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일 입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지않는 유저입니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
    }
}
