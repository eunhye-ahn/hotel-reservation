package com.hotel.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservation.dto.PriceTokenValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PriceTokenRedisService {
    private final RedisTemplate<String, Object> objectRedisTemplate;

    private static final long TTL_MINUTES = 10;
    private static final String PREFIX = "priceToken:";
    private final ObjectMapper redisObjectMapper;

    private String buildKey(String token){
        return PREFIX+token;
    }

    //토큰 저장
    public String save(int totalPrice, int numberOfRooms){
        String token = UUID.randomUUID().toString();
        PriceTokenValue value = PriceTokenValue.builder()
                .totalPrice(totalPrice)
                .numberOfRooms(numberOfRooms)
                .build();
        objectRedisTemplate.opsForValue()
                .set(buildKey(token), value, TTL_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    //토큰 조회 -가격 꺼내기
    public Optional<PriceTokenValue> get(String token){
        Object value = objectRedisTemplate.opsForValue().get(buildKey(token));
        if(value == null) return Optional.empty();
        return  Optional.of(redisObjectMapper.convertValue(value, PriceTokenValue.class));
    }

    //토큰 삭제 -일회성
    public void delete(String token){
        objectRedisTemplate.delete(buildKey(token));
    }
}
