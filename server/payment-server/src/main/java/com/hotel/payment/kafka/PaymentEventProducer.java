package com.hotel.payment.kafka;

import com.hotel.payment.dto.PaymentCompletedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * [WHAT] 결제완료 이벤트 발행
 * [WHY] 결제 승인 완료 후 reservation-server에 예약 상태 업데이트 요청
 *
 * [흐름]
 * payment-server -> Kafka "payment-completed" 토픽 발행
 * -> reservation-server Consumer 수신
 * -> 예약상태 PAID 업데이트
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentCompletedMessage> kafkaTemplate;

    private static final String TOPIC = "payment-completed";

    public void sendPaymentCompleted(PaymentCompletedMessage message){
        kafkaTemplate.send(TOPIC, message);
        log.info("payment-completed event publish - reservationKey: {}", message.getReservationKey());
    }
}
