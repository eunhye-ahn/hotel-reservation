package com.hotel.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [결제 준비 요청 DTO]
 *
 * [WHAT] 결제하기 버튼 클릭 시 전달하는 데이터
 * [WHY]  payment-server가 예약 확인 및 결제 준비하기 위해
 *
 * [흐름]
 * 클라이언트 → POST /api/v1/payments
 * → reservationKey로 예약 조회
 * → 결제 준비
 */
@Getter
@NoArgsConstructor
public class PaymentPrepareRequest {
    private String reservationKey;  // 예약 고유키
    private int amount;             // 결제 금액 (KRW)
}