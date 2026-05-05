package com.hotel.reservation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * # Redis 설정
 * # [WHY] RedisTemplate<String, Object>는 Spring Boot가 자동생성 안해줌
 * #       → RedisConfig에서 직접 빈 등록 필요
 * #       → RedisConfig의 @Value가 아래 host/port를 읽어옴
 * #
 * # [환경별 설정]
 * # 로컬  → application.yml (아래 설정)
 * # 도커  → docker-compose.yml의 environment
 * #         SPRING_DATA_REDIS_HOST: redis
 * #         SPRING_DATA_REDIS_PORT: 6379
 * #
 * # [AutoConfiguration과 차이]
 * # RedisTemplate<String, String> → Spring Boot 자동생성 (설정만 있으면 됨)
 * # RedisTemplate<String, Object> → 직접 빈 등록 필요 (RedisConfig)
 */
@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    // JWT용 (AuthService)
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // JavaTimeModule 등록 > localdatetime을 json으로 직렬화할때 jackson이 못읽음
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    // 멱등키용 (IdempotencyRedisService)
    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper()));
        return template;
    }
}
