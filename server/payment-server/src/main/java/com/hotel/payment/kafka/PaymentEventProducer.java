package com.hotel.payment.kafka;

import com.hotel.payment.dto.PaymentRetryMessage;
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
 * kafkaTemplate이 PaymentCompletedMessage를 JSON으로 직렬화해서 브로커로 전송
 * payment-completed 토픽의 파티션 중 하나에 저장됨
 * 전송 후 기다리지 않고 바로 리턴 (비동기)
 *
 * payment-server -> Kafka "payment-completed" 토픽 발행
 * -> reservation-server Consumer 수신
 * -> 예약상태 PAID 업데이트
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String COMPLETED_TOPIC = "payment-completed";
    private static final String RETRY_TOPIC = "payment-retry-queue";
    private static final String DLQ_TOPIC = "payment-dead-letter";

    public void sendPaymentCompleted(PaymentCompletedMessage message){
        //브로커로 전송 : 파티션 키(주문아이디)
        //예약서버-결제완료상태 알림 pending->paid
        kafkaTemplate.send(COMPLETED_TOPIC, message.getOrderId(), message);
        log.info("payment-completed event publish - orderId: {}", message.getOrderId());
    }

    public void sendToRetry(PaymentRetryMessage message){
        //브로커로 전송 : 파티션 키(주문아이디)
        //서버-결제상태처리재시도
        kafkaTemplate.send(RETRY_TOPIC, message.getOrderId(), message);
        log.info("retry queue publish - orderId : {}, retryCount: {}", message.getOrderId(), message.getRetryCount());
    }

    public void sendToDLQ(PaymentRetryMessage message){
        kafkaTemplate.send(DLQ_TOPIC, message.getOrderId(), message);
        log.info("DLQ publish - orderId : {}", message.getOrderId());
    }
}