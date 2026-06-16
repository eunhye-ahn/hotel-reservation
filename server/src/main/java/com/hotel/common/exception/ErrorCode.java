package com.hotel.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //jwt
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다."),

    //redis
    CART_FULL(HttpStatus.BAD_REQUEST, "장바구니는 최대 20개까지 담을 수 있습니다"),

    //valid
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),

    //hotel
    HOTEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 호텔입니다"),
    HOTEL_ALREADY_EXISTS(HttpStatus.CONFLICT, "같은 이름의 호텔이 존재합니다"),
    ROOM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 객실 유형입니다"),
    RATE_NOT_FOUND(HttpStatus.SERVICE_UNAVAILABLE, "오늘 요금 정보를 준비 중입니다"),
    ROOM_INVENTORY_NOT_FOUND(HttpStatus.SERVICE_UNAVAILABLE, "오늘 객실 정보를 준비 중입니다"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 객실 입니다"),
    ROOM_TYPE_ALREADY_EXISTS(HttpStatus.CONFLICT, "같은 이름의 객실 유형이 존재합니다"),
    INVALID_RESTORE(HttpStatus.INTERNAL_SERVER_ERROR, "재고 복구 중 오류가 발생했습니다"),

    //reservation
    RESERVATION_UNAVAILABLE(HttpStatus.CONFLICT, "예약 가능한 객실을 초과했습니다"),
    CANNOT_CANCEL_RESERVATION(HttpStatus.CONFLICT, "취소할 수 없는 예약입니다"),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 예약입니다"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "체크아웃 날짜는 체크인 날짜 이후여야 합니다"),
    EXCEED_MAX_OCCUPANCY(HttpStatus.BAD_REQUEST, "최대 수용 인원을 초과했습니다"),
    RESERVATION_CONFLICT(HttpStatus.CONFLICT, "일시적으로 예약이 집중되고 있습니다\n" +
            "잠시 후 다시 시도해주세요"),

    //결제
    PRICE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "세션이 만료되었습니다. 다시 시도해주세요"),
    PRICE_TOKEN_EXPIRED(HttpStatus.GONE, "가격 토큰이 만료되었습니다"),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 결제입니다"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 주문을 찾을 수 없습니다"),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 변조되었습니다"),
    MISSING_IDEMPOTENCY_KEY(HttpStatus.BAD_REQUEST, "Idempotency-Key 헤더가 필요합니다."),

    //reservationKey -멱등키
    HASH_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 다시 시도해주세요"),
    IDEMPOTENCY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 다시 시도해주세요"),
    IDEMPOTENCY_USER_MISMATCH(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    IDEMPOTENCY_REQUEST_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY, "잘못된 요청입니다"),
    IDEMPOTENCY_PROCESSING(HttpStatus.CONFLICT, "이미 처리 중인 요청이 있습니다"),
    IDEMPOTENCY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 다시 시도해주세요"),
    IDEMPOTENCY_UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 다시 시도해주세요"),

    //커서
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "유효하지 않은 커서입니다"),

    //user
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "이메일/비밀번호가 틀렸습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일 입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지않는 유저입니다"),

    //낙관적 락
    OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "요청이 충돌했습니다. 다시 시도해주세요");



    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, final String message){
        this.status = status;
        this.message = message;
    }
}
