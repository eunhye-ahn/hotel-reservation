package com.hotel.payment.service;

import com.hotel.payment.client.ReservationClient;
import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.dto.PaymentPrepareRequest;
import com.hotel.payment.dto.PaymentPrepareResponse;
import com.hotel.payment.dto.ReservationFeignResponse;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 결제준비 프로세스 처리
 * 클라이언트가 토스 결제창 열기 전 필요한 데이터 준비
 *
 * [흐름]
 * 1. reservation-server에 예약 유효성 확인
 * 2. paymentStatus PENDING 확인
 * 3. checkoutId 생성 (클라-서버 멱등키)
 * 4. payment_order_id 생성 (서버-PSP 멱등키)
 * 5. PAYMENT_EVENT 저장 (DB 2차 안전장치)
 * 6. PAYMENT_ORDER 저장
 * 7. payment_order_id, amount 반환
 */
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationClient reservationClient;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    @Transactional
    public PaymentPrepareResponse preparePayment(PaymentPrepareRequest request){
        //예약 유효성 확인
        ReservationFeignResponse reservation = reservationClient.getReservationForPayment(request.getReservationKey());

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
                .pspType("TOSS")
                .build();
        paymentEventRepository.save(paymentEvent);

        //PaymentOrder 저장
        PaymentOrder paymentOrder = PaymentOrder
                .builder()
                .paymentOrderId(paymentOrderId)
                .checkoutId(checkoutId)
                .sellerAccount(reservation.getSellerAccount())
                .amount(request.getAmount())
                .paymentOrderStatus(PaymentOrderStatus.NOT_STARTED)
                .build();
        paymentOrderRepository.save(paymentOrder);

        //paymentOrderId, amount 반환
        return PaymentPrepareResponse
                .builder()
                .paymentOrderId(paymentOrderId)
                .amount(request.getAmount())
                .build();
    }
}
