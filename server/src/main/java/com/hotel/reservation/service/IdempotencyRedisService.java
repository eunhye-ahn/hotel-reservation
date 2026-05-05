package com.hotel.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservation.dto.IdempotencyValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * [WHAT]
 * 멱등키 Redis 관리 서비스
 *
 * [WHY]
 * 같은 예약 요청이 여러번 와도 딱 한번만 처리하기 위해
 *
 * * [흐름]
 *  * tryProcessing() → NX로 선점 시도
 *  *      ├── true  → 내가 먼저 선점 → 예약 처리 진행
 *  *      └── false → 이미 있음 → get()으로 기존 값 조회
 *  *                    ├── processing → 409
 *  *                    ├── completed  → 200 (이전 결과 반환)
 *  *                    └── failed     → 500
 */

@Service
@RequiredArgsConstructor
public class IdempotencyRedisService {
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final ObjectMapper redisObjectMapper;

    private static final long TTL_HOURS = 24;
    private static final String PREFIX = "idempotency:";

    //키에 idempotency: 붙여서 저장-> redis에서 여러 데이터를 분류하기 위해
    private String buildKey(String reservationKey) {
        return PREFIX + reservationKey;
    }

    //redis저장 성공여부 : value 객체 직렬화 > redis저장 (setIfAbsent -> NX)
    //우선 redis를 선점하고 processing
    //그 다음 process 호출
    //redis에 멱등키가 없으면 processing->setIfAbsent->true반환->complete
    //redis에 멱등키가 이미 있으면 processing->setIfAbsent->false반환->이전요청으로 처리
    public boolean tryProcessing(String reservationKey, Long userId, String requestHash){
        IdempotencyValue value = IdempotencyValue.builder()
                .status("processing")
                .userId(userId)
                .requestHash(requestHash)
                .createdAt(LocalDateTime.now())
                .build();

        //redis저장 성공여부
        Boolean success = objectRedisTemplate.opsForValue()
                .setIfAbsent(buildKey(reservationKey), value, TTL_HOURS, TimeUnit.HOURS);

        return Boolean.TRUE.equals(success);
    }

    /**
     * [WHAT]
     * 기존 멱등키 조회
     *
     * [WHY]
     * tryProcessing()이 false일 때 (이미 있음)
     * 기존 값을 꺼내서 status 확인하기 위해
     *
     * [흐름]
     * Redis에서 key로 조회
     * ├── null → Optional.empty() (없음) -> NPE방지
     * └── 있음 → Optional.of(IdempotencyValue)
     */
    //멱등키 중복 조회 : value get() -> 있으면 value 없으면 Optional.empty() :NPE 방지
    public Optional<IdempotencyValue> get(String reservationKey){
        Object value = objectRedisTemplate.opsForValue().get(buildKey(reservationKey));
        if(value==null) return Optional.empty();
        IdempotencyValue idempotencyValue = redisObjectMapper.convertValue(value, IdempotencyValue.class);
        return Optional.of(idempotencyValue);
    }

    /**
     * [WHAT]
     * 예약 처리 완료 후 Redis 결과 업데이트
     *
     * [WHY]
     * processing → completed 로 상태 변경
     * 이후 같은 요청이 오면 이전 결과 그대로 반환하기 위해
     *
     * [흐름]
     * 기존 값 조회 → status를 completed로 변경 → 덮어씀
     */
    //예약 처리 완료 후 Redis 결과 업데이트 : 멱등키 중복조회 > processing -> complete로 상태 변경
    public void complete(String reservationKey){
        get(reservationKey).ifPresent(value -> {
            IdempotencyValue updated = IdempotencyValue.builder()
                    .status("completed")
                    .userId(value.getUserId())
                    .requestHash(value.getRequestHash())
                    .createdAt(value.getCreatedAt())
                    .build();
            objectRedisTemplate.opsForValue()
                    .set(buildKey(reservationKey), updated, TTL_HOURS, TimeUnit.HOURS);
        });
    }


    /**
     * [WHAT]
     * 예약 처리 실패 후 Redis 결과 업데이트
     *
     * [WHY]
     * processing → failed 로 상태 변경
     *
     * [흐름]
     * 기존 값 조회 → status를 failed로 변경 → 덮어씀
     */
//예약 처리 실패 후 Redis 결과 업데이트 : 멱등키 중복조회 > processing -> failed로 상태 변경
    public void fail(String reservationKey) {
        get(reservationKey).ifPresent(value -> {
            IdempotencyValue updated = IdempotencyValue.builder()
                    .status("failed")
                    .userId(value.getUserId())
                    .requestHash(value.getRequestHash())
                    .createdAt(value.getCreatedAt())
                    .build();
            objectRedisTemplate.opsForValue()
                    .set(buildKey(reservationKey), updated, TTL_HOURS, TimeUnit.HOURS);
        });
    }

}
