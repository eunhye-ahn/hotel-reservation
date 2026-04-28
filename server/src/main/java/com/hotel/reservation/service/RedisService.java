package com.hotel.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    /**
     *     redis 통신 객체 - 키값구조
     *     [RT관리]
     *     ---------key ------------value
     *     RT:유저id, refreshtoken(, ttl, 시간단위지정 )
     *                                    =================== ttl 함께 저장하면 자동삭제됨
     *     [BL_AT관리]
     *     --------------key------value
     *     BL:accessToken, logout
     */
    private final RedisTemplate<String,String> redisTemplate;

    //로그인,재발급 -rt저장
    public void saveRefreshToken(Long userId, String refreshToken, Long expiration){
        redisTemplate.opsForValue()
                .set("RT:"+userId, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    //로그아웃 -rt삭제
    public void deleteRefreshToken(Long userId){ redisTemplate.delete("RT:"+userId);}

    //재발급시, 쿠키rt유효성검사 -rt 조회
    public String getRefreshToken(Long userId){return redisTemplate.opsForValue().get("RT:"+userId);}

    //로그아웃 -at bl등록
    public void saveBlackList(String accessToken, Long remainingExpiration){
        redisTemplate.opsForValue()
                .set("BL:"+accessToken, "logout", remainingExpiration, TimeUnit.MILLISECONDS);
    }

    //로그인 있는지없는지여부 확인-bl조회
    public boolean isBlackList(String accessToken) { return redisTemplate.hasKey("BL:"+accessToken);}
}
