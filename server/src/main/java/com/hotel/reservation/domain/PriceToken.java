package com.hotel.reservation.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

//예약폼 진입 시, 임시 가격 고정을 위한 토큰 테이블

@Entity
@Table(name="price_token")
@Getter
@NoArgsConstructor
public class PriceToken extends BaseTime{
    @Id
    private String token;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    @Builder
    public PriceToken(int totalPrice){
        this.token = UUID.randomUUID().toString();
        this.totalPrice = totalPrice;
        this.expiredAt = LocalDateTime.now().plusMinutes(10);
    }
}
