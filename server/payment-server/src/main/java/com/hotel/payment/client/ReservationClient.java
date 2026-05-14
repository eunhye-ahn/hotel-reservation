package com.hotel.payment.client;

import com.hotel.payment.dto.ReservationFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * [WHAT] reservation-server에 예약 유효성 확인 요청
 * [WHY] 결제 전 예약상태 (PENDING) 및 정산 계좌 확인
 *
 * [흐름]
 * payment-server
 * ->ReservationClient.getReservationForPayment() 호출
 * ->reservation-server가 예약정보반환
 * ->payment-server가 결제처리
 */
@FeignClient(name="reservation-service", url="${reservation.service.url}")
public interface ReservationClient {
    @GetMapping("/api/v1/reservations/{reservationKey}/payment-info")
    ReservationFeignResponse getReservationForPayment(@PathVariable String reservationKey);
}
