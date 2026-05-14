package com.hotel.payment.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * [결제 준비 응답 DTO]
 *
 * [WHAT] 결제 준비 완료 후 클라이언트에 반환
 * [WHY] 클라이언트가 토스 SDK로 결제창 열 때 필요
 *
 * [흐름]
 * payment-server → 클라이언트
 * → 클라이언트가 paymentOrderId를 orderId로 토스 SDK 호출
 * → 토스 외부 결제창 열림
 */
@Getter
@Builder
public class PaymentPrepareResponse {
    private String paymentOrderId;  // 토스 SDK orderId로 사용
    private int amount;             // 결제 금액 검증용
}