package com.hotel.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [WHAT] 결제완료 Kafka 메시지
 * [WHY] payment-server로부터 결제 완료 이벤트 수신
 *
 * [필드]
 * reservationKey -예약식별자
 * reservationId -예약 ID
 */
@Getter
@NoArgsConstructor
public class PaymentCompletedMessage {
    private String orderId;
}
