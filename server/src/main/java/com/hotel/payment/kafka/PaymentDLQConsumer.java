package com.hotel.payment.kafka;

import com.hotel.payment.dto.PaymentRetryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * [WHAT] DLQ
 * [WHY] 재시도 임계값을 초과한 실패건 격리 => 수동 조사 필요
 *
 * [흐름]
 * payment-dead-letter 수신
 * -> 로그기록
 * -> 슬랙알림, 관리자 페이지 등으로 연동
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDLQConsumer {

    @KafkaListener(topics="payment-dead-letter", groupId = "payment-dlq-group")
    public void consumeDLQ(PaymentRetryMessage message){
        log.error("DLQ - 수동처리 필요 - orderId : {}, retryCount : {}", message.getOrderId(), message.getRetryCount());

        //슬랙알림,db저장 등
    }
}
