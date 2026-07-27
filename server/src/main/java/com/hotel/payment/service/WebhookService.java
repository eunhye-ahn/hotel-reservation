package com.hotel.payment.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.dto.TossWebhookRequest;
import com.hotel.payment.dto.TossWebhookResponse;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentProcessService paymentProcessService;

    @Retryable(
            retryFor = DataAccessException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public TossWebhookResponse handleWebhook(TossWebhookRequest request){

        //payment_status_changed 이벤트만 처리
        if(!request.getEventType().equals("PAYMENT_STATUS_CHANGED")){
            log.info("not wanted event type : {}", request.getEventType());
            return null;
        }

        String orderId = request.getData().getOrderId();
        String status = request.getData().getStatus();
        String paymentKey = request.getData().getPaymentKey();

        PaymentOrder paymentOrder = paymentOrderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        PaymentEvent paymentEvent = paymentEventRepository.findByCheckoutId(paymentOrder.getCheckoutId())
                .orElseThrow();

        //paymentOrderId로 멱등성 체크
        if(!paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.NOT_STARTED)){
            log.info("already processed payments - orderId: {}", paymentOrder.getPaymentOrderStatus());
            return new TossWebhookResponse(paymentEvent.getReservationKey());
        }

        //상태변경
        switch (status){
            case "DONE" -> {
                paymentProcessService.processDone(orderId, paymentKey, paymentOrder, paymentEvent);
                log.info("payment completed processed- orderId : {}", request.getData().getOrderId());
            }
            case "CANCELED", "ABORTED", "EXPIRED", "PARTIAL_CANCELED" -> {
                //결제실패처리 -> 상태만 변경하므로 트랜잭션분리X
                paymentOrder.fail();
                paymentOrderRepository.save(paymentOrder);
                log.info("payment failed processed- orderId : {}", request.getData().getOrderId());
            }
            default -> log.warn("unknown payment status : {}", status);
        }
        return new TossWebhookResponse(paymentEvent.getReservationKey());
    }

    @Recover
    public TossWebhookResponse recover(DataAccessException e, TossWebhookRequest request){
        log.error("웹훅 처리 재시도 모두 실패 - orderId: {}", request.getData().getOrderId(), e);
        // 실패기록 테이블 저장
        throw new CustomException(ErrorCode.PAYMENT_PROCESSING_FAILED);
    }
}
