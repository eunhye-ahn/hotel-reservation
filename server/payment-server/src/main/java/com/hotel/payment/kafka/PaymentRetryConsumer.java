package com.hotel.payment.kafka;

import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.dto.PaymentCompletedMessage;
import com.hotel.payment.dto.PaymentRetryMessage;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.service.PaymentProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentRetryConsumer {
    private final PaymentProcessService paymentProcessService;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "payment-retry-queue", groupId = "payment-retry-group")
    public void consumeRetry(PaymentRetryMessage message){
        log.info("retry queue consume - orderId : {}, retryCount:{}", message.getOrderId(), message.getRetryCount());

        //orderId로 실패한 결제주문조회
        PaymentEvent event = paymentEventRepository.findByOrderId(message.getOrderId())
                .orElseThrow();

        PaymentOrder paymentOrder = paymentOrderRepository.findByCheckoutId(event.getCheckoutId())
                .orElseThrow();

        //상태재시도
        try {
            //db트랜잭션과 카프카발행을 분리하기위해 별도 클래스로 분리
            paymentProcessService.processDone(message.getOrderId(), event.getPspToken(), paymentOrder, event);

            paymentEventProducer.sendPaymentCompleted(
                    new PaymentCompletedMessage(
                            event.getOrderId()
                    )
            );

            log.info("payment completed processed- orderId : {}", message.getOrderId());
        }catch(DataAccessException e){
            //DB 일시적 오류 -> 재시도가능
            log.error("DB오류 - orderId : {}", message.getOrderId(), e);
            //재시도 횟수 초과했을 경우
            if(paymentOrder.isRetryExhausted()){
                paymentEventProducer.sendToDLQ(
                        new PaymentRetryMessage(message.getOrderId(), paymentOrder.getRetryCount())
                );
            }else{
                //재시도 카운트 ++
                paymentOrder.incrementRetryCount();
                paymentOrderRepository.save(paymentOrder); //트랜잭션으로 안묶어서 명시적 저장(더티체킹 보장X)
                paymentEventProducer.sendToRetry(
                        new PaymentRetryMessage(message.getOrderId(), paymentOrder.getRetryCount())
                );
            }
        }catch(Exception e){
            //재시도 불가 오류
            log.error("retry impossible error - orderId: {}", message.getOrderId(), e);
        }
    }
}
