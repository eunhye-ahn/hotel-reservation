package com.hotel.reservation.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh-expiration}")
    private Long expiration;

    public ResponseCookie createRTCookie(String refreshToken){
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(expiration)
                .secure(false)
                .build();
    }

    public ResponseCookie deleteRTCookie(){
        return ResponseCookie.from("refreshToken","")
                .httpOnly(true)
                .path("/")
                .maxAge(0)  //브라우저가 쿠키 즉시삭제
                .secure(false)
                .build();
    }
}
