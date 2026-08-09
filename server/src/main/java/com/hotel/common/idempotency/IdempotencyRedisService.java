package com.hotel.common.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 */
@Service
@RequiredArgsConstructor
public class IdempotencyRedisService {
    //redis 저장/조회용 (직렬화)
    private final RedisTemplate<String, Object> objectRedisTemplate;
    //redis에서 꺼낸 object (역직렬화)
    private final ObjectMapper redisObjectMapper;
    //redis-멱등키
    private static final String PREFIX = "idempotency:";

    //redis key 형태 : idempotency:{도메인}:{원본키}
    private String buildKey(IdempotencyDomain domain, String key) {
        return PREFIX + domain.name().toLowerCase() + ":" + key;
    }

    /**
     * 멱등키 선점 시도
     * - setIfAbsent(SETNX) : key가 없을때만 저장 성공 -> 원자적 연산이라 동시요청에도 안전
     * - 선점 성공 시 status = "processing" 기록, TTL은 도메인의 processing TTL 사용
     * - 반환값 true -> 내가 선점 성공 -> 컨트롤러/서비스 로직 진행해도 됨
     * - 반환값 false -> 이미 누가 선점 -> get()으로 기존 상태 확인 필요
     */
    public boolean tryProcessing(IdempotencyDomain domain, String key, Long userId, String requestHash){
        IdempotencyValue value = IdempotencyValue.builder()
                .status("processing")
                .userId(userId)
                .requestHash(requestHash)
                .createdAt(LocalDateTime.now())
                .build();
        /**
         * redis저장 성공여부
         * setIfAbsent(key, value, Duration) 오버로드 사용
         * - redis -> nill (이미있음 저장실패)
         *          -> redisTemplate -> null (Boolean) -> Boolean.TRUE.equals(success) :NPE방지 -> false
         */
        Boolean success = objectRedisTemplate.opsForValue()
                .setIfAbsent(buildKey(domain, key), value, domain.getTtlByStatus("processing"));

        //equals => NPE 방지
        return Boolean.TRUE.equals(success);
    }


    //기존 멱등키 조회
    public Optional<IdempotencyValue> get(IdempotencyDomain domain, String key){
        Object value = objectRedisTemplate.opsForValue().get(buildKey(domain, key));
        if(value==null) return Optional.empty();
        return  Optional.of(redisObjectMapper.convertValue(value, IdempotencyValue.class));
    }

    //처리 완료 후 Redis 결과 업데이트 : processing -> complete로 상태 변경
    public void complete(IdempotencyDomain domain, String key){
        updateStatus(domain, key, "completed");
    }



    //처리 실패 후 Redis 결과 업데이트 : processing -> failed로 상태 변경
    public void fail(IdempotencyDomain domain, String key) {
        updateStatus(domain, key, "failed");
    }


    //complet(), fail() 공통로직: 멱등키중복검사->상태업데이트
    private void updateStatus(IdempotencyDomain domain, String key, String newStatus){
        get(domain, key).ifPresent(value -> {
            IdempotencyValue updated = IdempotencyValue.builder()
                    .status(newStatus)
                    .userId(value.getUserId())
                    .requestHash(value.getRequestHash())
                    .createdAt(value.getCreatedAt())
                    .build();
            objectRedisTemplate.opsForValue()
                    .set(buildKey(domain, key), updated, domain.getTtlByStatus(newStatus));
        });
    }


}
