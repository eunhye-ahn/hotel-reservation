package com.hotel.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class IdempotencyValue {
    private String status;      // processing | completed | failed
    private Long userId;        // 다른 유저 방지
    private String requestHash; // 요청 본문 해시 (변조 감지)
    private LocalDateTime createdAt;
}
