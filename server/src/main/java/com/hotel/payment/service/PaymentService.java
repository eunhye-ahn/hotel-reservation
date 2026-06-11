package com.hotel.payment.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.payment.client.TossPaymentClient;
import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.dto.*;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
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
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final TossPaymentClient tossPaymentClient;
    private final ReservationRepository reservationRepository;

    @Value("${psp.toss.secret-key}")
    private String secretKey;

    @Transactional
    public PaymentPrepareResponse preparePayment(String reservationKey, String orderId, HttpServletRequest request){
        //예약 유효성 확인
        Reservation reservation = reservationRepository.findByReservationKey(reservationKey)
                .orElseThrow();

        //paymentStatus PENDING 확인
        if(!reservation.getPaymentStatus().equals(PaymentStatus.PENDING)){
            throw new IllegalStateException("결제 가능한 예약이 아닙니다.");
        }

        //기존 paymentEvent있으면 재사용 :결제창닫고 다시 결제를 열 경우 예약ID unique 방지
        Optional<PaymentEvent> existingEvent = paymentEventRepository.findByReservationId(reservation.getId());
        if(existingEvent.isPresent()){
            PaymentOrder existingOrder = paymentOrderRepository.findByCheckoutId(existingEvent.get().getCheckoutId())
                    .orElseThrow();
            return PaymentPrepareResponse.builder()
                    .paymentOrderId(existingOrder.getPaymentOrderId())
                    .amount(existingOrder.getAmount())
                    .userId(reservation.getUser().getId())
                    .build();
        }

        //checkoutId 가져오기 (클라-서버 멱등키)
        String checkoutId = request.getHeader("Idempotency-Key");

        if(checkoutId == null) {
            throw new CustomException(ErrorCode.MISSING_IDEMPOTENCY_KEY);
        }

        //paymentOrderId 생성 (서버-PSP 멱등키)
        String paymentOrderId = UUID.randomUUID().toString();

        //PaymentEvent 저장
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .checkoutId(checkoutId)
                .userId(reservation.getUser().getId())
                .orderId(orderId)
                .reservationId(reservation.getId())
                .reservationKey(reservationKey)
                .pspType("TOSS")
                .build();
        paymentEventRepository.save(paymentEvent);

        //PaymentOrder 저장
        PaymentOrder paymentOrder = PaymentOrder
                .builder()
                .paymentOrderId(paymentOrderId)
                .checkoutId(checkoutId)
                .sellerAccount(reservation.getHotel().getSellerAccount())
                .amount(reservation.getTotalPrice())
                .paymentOrderStatus(PaymentOrderStatus.NOT_STARTED)
                .build();
        paymentOrderRepository.save(paymentOrder);

        //paymentOrderId, amount 반환
        return PaymentPrepareResponse
                .builder()
                .paymentOrderId(paymentOrderId)
                .amount(reservation.getTotalPrice())
                .build();
    }

    public String getReservationKey(String orderId){
        PaymentOrder paymentOrder = paymentOrderRepository.findById(orderId)
                .orElseThrow(()->new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        PaymentEvent paymentEvent = paymentEventRepository.findByCheckoutId(paymentOrder.getCheckoutId())
                .orElseThrow();
        return paymentEvent.getReservationKey();
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

        //금액위변조 감지
        //db에 저장된 amount와 클라이언트가 보낸 amount 비교
        if(paymentOrder.getAmount() != request.getAmount()){
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        //토스 승인 API 호출
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
        tossPaymentClient.confirm(
                "Basic " + encodedKey,
                new TossConfirmRequest(request.getPaymentKey(), request.getOrderId(), request.getAmount())
        );

        PaymentEvent paymentEvent = paymentEventRepository
                .findByCheckoutId(paymentOrder.getCheckoutId())
                .orElseThrow();

        return new PaymentConfirmResponse(paymentEvent.getReservationKey());
    }
}