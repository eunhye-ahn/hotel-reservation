package com.hotel.payment.service;

import com.hotel.payment.client.ReservationClient;
import com.hotel.payment.client.TossPaymentClient;
import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.dto.*;
import com.hotel.payment.exception.CustomException;
import com.hotel.payment.exception.ErrorCode;
import com.hotel.payment.kafka.PaymentEventProducer;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

/**
 * 결제 프로세스 처리
 *
 *
 * [결제준비 흐름 -클라이언트가 토스 결제창 열기 전 필요한 데이터 준비]
 * 1. reservation-server에 예약 유효성 확인
 * 2. paymentStatus PENDING 확인
 * 3. checkoutId 생성 (클라-서버 멱등키)
 * 4. payment_order_id 생성 (서버-PSP 멱등키)
 * 5. PAYMENT_EVENT 저장 (DB 2차 안전장치)
 * 6. PAYMENT_ORDER 저장
 * 7. payment_order_id, amount 반환
 *
 * [결제승인 흐름 -]
 * 1. PaymentOrder 도메인 검사 (존재여부, 상태확인)
 * 2. 상태 EXECUTING으로 변경
 * 3. 토스 승인 API 호출
 * 4. 상태 SUCCESS로 변경
 * 5. PaymentEvent 완료처리
 */

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationClient reservationClient;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final TossPaymentClient tossPaymentClient;
    private final PaymentEventProducer paymentEventProducer;

    @Value("${psp.toss.secret-key}")
    private String secretKey;

    @Transactional
    public PaymentPrepareResponse preparePayment(String reservationKey){
        //예약 유효성 확인
        ReservationFeignResponse reservation = reservationClient.getReservationForPayment(reservationKey);


        //paymentStatus PENDING 확인
        if(!reservation.getPaymentStatus().equals("PENDING")){
            throw new IllegalStateException("결제 가능한 예약이 아닙니다.");
        }

        //checkoutId 생성 (클라-서버 멱등키)
        String checkoutId = UUID.randomUUID().toString();

        //paymentOrderId 생성 (서버-PSP 멱등키)
        String paymentOrderId = UUID.randomUUID().toString();

        //PaymentEvent 저장
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .checkoutId(checkoutId)
                .reservationId(reservation.getReservationId())
                .reservationKey(reservationKey)
                .pspType("TOSS")
                .build();
        paymentEventRepository.save(paymentEvent);

        //PaymentOrder 저장
        PaymentOrder paymentOrder = PaymentOrder
                .builder()
                .paymentOrderId(paymentOrderId)
                .checkoutId(checkoutId)
                .sellerAccount(reservation.getSellerAccount())
                .amount(reservation.getAmount())
                .paymentOrderStatus(PaymentOrderStatus.NOT_STARTED)
                .build();
        paymentOrderRepository.save(paymentOrder);

        //paymentOrderId, amount 반환
        return PaymentPrepareResponse
                .builder()
                .paymentOrderId(paymentOrderId)
                .amount(reservation.getAmount())
                .build();
    }

    //결제승인
    @Transactional
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request){
        //도메인검사
        PaymentOrder paymentOrder = paymentOrderRepository.findById(request.getOrderId())
                .orElseThrow(()-> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        if(!paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.NOT_STARTED)){
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        //PaymentOrderStatus 상태변경(Pendig -> executing)
        paymentOrder.executing();

        //토스 승인 API 호출
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
        tossPaymentClient.confirm(
                "Basic " + encodedKey,
                new TossConfirmRequest(request.getPaymentKey(), request.getOrderId(), request.getAmount())
        );

        //PaymentOrderStatus 상태변경(executing -> success)
        paymentOrder.success();

        //paymentEvent 완료처리(isPayDone=true)
        PaymentEvent paymentEvent = paymentEventRepository.findByCheckoutId(paymentOrder.getCheckoutId())
                .orElseThrow();
        paymentEvent.complete(request.getPaymentKey());

        //kafka 결제완료 이벤트 발행(paymentStatus -> paid로 변경하기 위해)
        paymentEventProducer.sendPaymentCompleted(
                new PaymentCompletedMessage(
                        paymentEvent.getReservationKey(),
                        paymentEvent.getReservationId()
                )
        );

        return new PaymentConfirmResponse(paymentEvent.getReservationKey());
    }
}
