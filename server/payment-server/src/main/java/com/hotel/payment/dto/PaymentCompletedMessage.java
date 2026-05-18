package com.hotel.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [WHAT] 결제 완료 Kafka 메시지 dto
 * [WHY] payment-server -> reservation-server 결제 완료 이벤트 전달
 *
 * [필드]
 * reservationKey - 예약식별자 (reservation-server에서 상태 업데이트)
 * reservationId - 예약ID
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedMessage {
    private String reservationKey;
    private Long reservationId;
}
