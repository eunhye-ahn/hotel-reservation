package com.hotel.reservation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * HttpStatus.OK                    // 200  조회, 수정, 삭제 성공
 * HttpStatus.CREATED               // 201  생성 성공 (회원가입, 예약 생성)
 * HttpStatus.BAD_REQUEST           // 400  잘못된 요청 (유효성 검사 실패, 잘못된 파라미터)
 * HttpStatus.UNAUTHORIZED          // 401  인증 안됨 (토큰 없음, 만료, 유효하지 않음)
 * HttpStatus.FORBIDDEN             // 403  인증은 됐지만 권한 없음 (일반유저가 관리자 API 호출)
 * HttpStatus.NOT_FOUND             // 404  리소스 없음 (없는 호텔, 없는 예약)
 * HttpStatus.CONFLICT              // 409  충돌 (동시 예약, 이미 취소된 예약 재취소)
 * HttpStatus.INTERNAL_SERVER_ERROR // 500  서버 내부 오류 (예상치 못한 에러)
 * HttpStatus.SERVICE_UNAVAILABLE   // 503  서비스 일시 불가 (요금 미등록, 점검 중)
 */

@Getter
public enum ErrorCode {
    //jwt
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다."),

    //valid
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),

    //hotel
    HOTEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 호텔입니다"),
    ROOM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않은 객실 유형입니다"),
    RATE_NOT_FOUND(HttpStatus.SERVICE_UNAVAILABLE, "오늘 요금 정보를 준비 중입니다"),
    ROOM_INVENTORY_NOT_FOUND(HttpStatus.SERVICE_UNAVAILABLE, "오늘 객실 정보를 준비 중입니다"),

    //reservation
    RESERVATION_UNAVAILABLE(HttpStatus.CONFLICT, "예약 가능한 객실을 초과했습니다"),

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
