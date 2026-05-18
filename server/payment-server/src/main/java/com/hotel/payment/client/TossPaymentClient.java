package com.hotel.payment.client;

import com.hotel.payment.dto.TossConfirmRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * [WHAT] 토스페이먼츠 결제 승인 요청 클라이언트
 * [WHY] 프론트에서 결제창 완료 후 실제 승인 처리를 위해 토스 API 호출
 *
 * [흐름]
 * FE : redirecturl로 리다이렉트 -> payment-server
 *
 *
 * -> TossPaymentClient.confirm() 호출
 * -> 토스페이먼츠 승인 API (/v1/payments/confirm) 호출
 * -> 승인 완료 시 PaymentOrder 상태 SUCESS로 업데이트
 */
@FeignClient(name="toss-payments", url = "https://api.tosspayments.com")
public interface TossPaymentClient {
    /**
     * 토스페이먼츠 결제 승인 API 호출
     * @param authorization     Base64로 인코딩된 키 (Basic {encodedKey})
     * @param request
     */
    @PostMapping(value="/v1/payments/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    void confirm(@RequestHeader("Authorization") String authorization, @RequestBody TossConfirmRequest request);
}
