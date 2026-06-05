package com.hotel.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *   PAYMENT_EVENT {
 *     varchar checkout_id PK
 *     bigint reservation_id FK
 *     varchar psp_token
 *     boolean is_payment_done
 *     timestamp created_at
 *   }
 */
@Table(name="payment_event")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentEvent extends BaseTime{
    //PK,멱등키
    @Id
    private String checkoutId;

    //unique, Kafka 통신용
    @Column(nullable = false, unique = true)
    private Long reservationId;

    //분산트랜잭션 식별키
    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private Long userId;

    //리다이렉트용
    @Column(nullable = false)
    private String reservationKey;

    //Toss,KAKAO
    @Column(nullable = false)
    private String pspType;

    //null허용 (PSP 등록 후 받음)
    private String pspToken;

    //결제완료여부
    @Builder.Default
    private boolean isPaymentDone = false;

    public void complete(String pspToken){
        this.pspToken = pspToken;
        this.isPaymentDone = true;
    }
}