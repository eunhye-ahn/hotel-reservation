package com.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [WHAT] 결제 재시도 메시지 DTO
 * 재시도 큐/DLQ에 발행할 메시지
 *
 * 재시도큐 - 실패한 메시지를 다시 처리 시도 -> 처리
 * dlq - 재시도를 너무 많이 해도 계속 실패한 메시지 -> 개발자알림 -> 수동처리
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRetryMessage {
    private String orderId;
    private int retryCount;
}
