package com.hotel.payment.kafka;

import com.hotel.payment.domain.PaymentRetryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentRetryConsumer {
    @KafkaListener(topics = "payment-retry-queue", groupId = "payment-retry-group")
    public void consumeRetry(PaymentRetryMessage message){
        log.info("retry queue consume - orderId : {}, retryCount:{}", message.getOrderId(), message.getRetryCount());

        Payment
    }
}
