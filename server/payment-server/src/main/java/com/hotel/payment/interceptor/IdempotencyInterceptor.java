package com.hotel.payment.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.dto.PaymentPrepareRequest;
import com.hotel.payment.dto.PaymentPrepareResponse;
import com.hotel.payment.repository.PaymentEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * 멱등성 인터셉터
 * [WHAT] 중복결제요청 방지
 * []
 *
 * [흐름]
 * 클라이언트 → Idempotency-Key 헤더 전송
 * → DB에서 checkout_id 조회
 *    → 있으면 → 기존 응답 반환 (컨트롤러 진입 차단)
 *    → 없으면 → 통과
 *
 * 2차 안전장치: PAYMENT_EVENT.checkout_id PK 제약
 */

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {
    private final PaymentEventRepository paymentEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{
        //Idempotency-Key 헤더 확인
        String checkoutId = request.getHeader("Idempotency-Key");

        //헤더 없으면 통과
        if(checkoutId == null){
            return true;
        }

        //DB에서 checkout_id 조회
        Optional<PaymentEvent> paymentEvent = paymentEventRepository.findById(checkoutId);

        if(paymentEvent.isPresent()){
            //중복요청 -> 기존 결과 반환
            PaymentEvent event = paymentEvent.get();
            PaymentPrepareResponse cachedResponse = PaymentPrepareResponse
                    .builder()
                    .paymentOrderId(event.getCheckoutId())
                    .amount(0) //paymentOrder에서 조회필요
                    .build();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(cachedResponse));
            return false;             //컨트롤러 진입차단
        }

        return true;
    }
}
