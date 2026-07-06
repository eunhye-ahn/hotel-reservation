package com.hotel.reservation.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

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

    //위시컬렉션 쿠키
    public ResponseCookie createWishCollectionCookie(Long collectionId){
        return ResponseCookie.from("wish-collection",String.valueOf(collectionId))
                .httpOnly(true)
                .path("/api/v1/wish")
                .maxAge(Duration.ofDays(7).getSeconds())
                .secure(false)
                .build();

    }

    public ResponseCookie deleteWishCollectionCookie(Long collectionId){
        return ResponseCookie.from("wish-collection","")
                .httpOnly(true)
                .path("/api/v1/wish")
                .maxAge(0)
                .secure(false)
                .build();
    }
}
