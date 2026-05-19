package com.hotel.payment.service;

import com.hotel.payment.domain.*;
import com.hotel.payment.dto.PaymentCompletedMessage;
import com.hotel.payment.dto.TossWebhookRequest;
import com.hotel.payment.dto.TossWebhookResponse;
import com.hotel.payment.exception.CustomException;
import com.hotel.payment.exception.ErrorCode;
import com.hotel.payment.kafka.PaymentEventProducer;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final LedgerRepository ledgerRepository;

    @Transactional
    public TossWebhookResponse handleWebhook(TossWebhookRequest request){
        log.info("webhook - eventType: {}. status: {}", request.getEventType(), request.getData().getStatus());

        //payment_status_changed 이벤트만 처리
        if(!request.getEventType().equals("PAYMENT_STATUS_CHANGED")){
            log.info("not wanted event type : {}", request.getEventType());
        }

        String orderId = request.getData().getOrderId();
        String status = request.getData().getStatus();
        String paymentKey = request.getData().getPaymentKey();

        //paymentOrderId로 멱등성 체크
        PaymentOrder paymentOrder = paymentOrderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        if(!paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.NOT_STARTED)){
            log.info("already processed payments - orderId: {}", paymentOrder.getPaymentOrderStatus());
        }
        PaymentEvent paymentEvent = paymentEventRepository.findByCheckoutId(paymentOrder.getCheckoutId())
                .orElseThrow();
        //상태변경
        switch (status){
            case "DONE" -> {
                paymentOrder.success();
                paymentEvent.complete(paymentKey);

                paymentEventProducer.sendPaymentCompleted(
                        new PaymentCompletedMessage(
                                paymentEvent.getReservationKey(),
                                paymentEvent.getReservationId()
                        )
                );
                //판매자
                ledgerRepository.save(Ledger.builder()
                        .paymentOrderId(orderId)
                        .account(paymentOrder.getSellerAccount())
                        .accountType(AccountType.BUYER)
                        .debit(paymentOrder.getAmount())
                        .credit(null)
                        .build()
                );
                //구매자
                ledgerRepository.save(Ledger.builder()
                        .paymentOrderId(orderId)
                        .account(paymentOrder.)
                        .accountType()
                        .debit()
                        .credit()
                        .build()
                );
                log.info("payment completed processed- orderId : {}", request.getData().getOrderId());
            }
            case "CANCELED", "ABORTED", "EXPIRED", "PARTIAL_CANCELED" -> {
                paymentOrder.fail();
                log.info("payment failed processed- orderId : {}", request.getData().getOrderId());
            }
            default -> log.warn("unknown payment status : {}", status);
        }

        return new TossWebhookResponse(paymentEvent.getReservationKey());
    }
}
