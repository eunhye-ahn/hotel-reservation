package com.hotel.payment.service;

import com.hotel.payment.domain.*;
import com.hotel.payment.dto.PaymentCompletedMessage;
import com.hotel.payment.dto.PaymentRetryMessage;
import com.hotel.payment.dto.TossWebhookRequest;
import com.hotel.payment.dto.TossWebhookResponse;
import com.hotel.payment.exception.CustomException;
import com.hotel.payment.exception.ErrorCode;
import com.hotel.payment.kafka.PaymentEventProducer;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * [WHAT] 토스페이먼츠 웹훅 처리 서비스
 * [WHY] PSP에서 비동기로 결제 상태 변경 알림을 받아 처리
 *
 * [흐름]
 * 1. 이벤트 타입 확인
 * 2. 결제상태확인
 * 3. DONE -> PaymentOrderStatus SUCCESS + PaymentEvnet is_done=true + kafka 발행
 * 4. CANCELED / ABORTED / EXPIRED / PARTIAL_CANCELED → PaymentOrder FAILED 처리
 *
 * [멱등성]
 * 웹훅은 중복 발송될 수 있으므로
 * 이미 SUCCESS인 경우 중복처리 방지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final PaymentProcessService paymentProcessService;

    public TossWebhookResponse handleWebhook(TossWebhookRequest request){
        log.info("webhook - eventType: {}. status: {}", request.getEventType(), request.getData().getStatus());

        //payment_status_changed 이벤트만 처리
        if(!request.getEventType().equals("PAYMENT_STATUS_CHANGED")){
            log.info("not wanted event type : {}", request.getEventType());
            return null;
        }

        String orderId = request.getData().getOrderId();
        String status = request.getData().getStatus();
        String paymentKey = request.getData().getPaymentKey();

        //paymentOrderId로 멱등성 체크
        PaymentOrder paymentOrder = paymentOrderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        PaymentEvent event = paymentEventRepository.findByCheckoutId(paymentOrder.getCheckoutId()).orElseThrow();
        if(!paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.NOT_STARTED)){
            log.info("already processed payments - orderId: {}", paymentOrder.getPaymentOrderStatus());
            return new TossWebhookResponse(event.getReservationKey());
        }
        PaymentEvent paymentEvent = paymentEventRepository.findByCheckoutId(paymentOrder.getCheckoutId())
                .orElseThrow();
        //상태변경
        switch (status){
            case "DONE" -> {
                try {
                    //db트랜잭션과 카프카발행을 분리하기위해 별도 클래스로 분리
                    paymentProcessService.processDone(orderId, paymentKey, paymentOrder, paymentEvent);

                    paymentEventProducer.sendPaymentCompleted(
                            new PaymentCompletedMessage(
                                    paymentEvent.getOrderId()
                            )
                    );

                    log.info("payment completed processed- orderId : {}", request.getData().getOrderId());
                }catch(DataAccessException e){
                    //DB 일시적 오류 -> 재시도가능
                    log.error("DB오류 - orderId : {}", orderId, e);
                    //재시도 횟수 초과했을 경우
                    if(paymentOrder.isRetryExhausted()){
                        paymentEventProducer.sendToDLQ(
                                new PaymentRetryMessage(orderId, paymentOrder.getRetryCount())
                        );
                    }else{
                        //재시도 카운트 ++
                        paymentOrder.incrementRetryCount();
                        paymentOrderRepository.save(paymentOrder); //트랜잭션으로 안묶어서 명시적 저장(더티체킹 보장X)
                        paymentEventProducer.sendToRetry(
                                new PaymentRetryMessage(orderId, paymentOrder.getRetryCount())
                        );
                    }
                }catch(Exception e){
                    //재시도 불가 오류
                    log.error("retry impossible error - orderId: {}", orderId, e);
                }
            }
            case "CANCELED", "ABORTED", "EXPIRED", "PARTIAL_CANCELED" -> {
                paymentOrder.fail();
                paymentOrderRepository.save(paymentOrder);
                log.info("payment failed processed- orderId : {}", request.getData().getOrderId());
            }
            default -> log.warn("unknown payment status : {}", status);
        }

        return new TossWebhookResponse(paymentEvent.getReservationKey());
    }
}
