package com.hotel.reservation.kafka;

import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.dto.PaymentCompletedMessage;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * [WHAT] 결제완료 이벤트 수신
 * [WHY] payment-server로부터 결제 완료 이벤트 받아서 예약 결제상태 PAID로 업데이트
 *
 * [흐름]
 * payment-server -> Kafka "payment-completed" 토픽
 * -> PaymentEnventConsumer 수신
 * -> paymentstatus 상태 PAID 업데이트
 */
@Component
@Slf4j
public class PaymentEventConsumer {
    private final ReservationRepository reservationRepository;

    public PaymentEventConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    @KafkaListener(topics="payment-completed", groupId = "reservation-group")
    public void consumerPaymentCompleted(PaymentCompletedMessage message){
        log.info("payment completed event subscribe - reservationKey: {}", message.getReservationKey());

        Reservation reservation = reservationRepository.findByReservationKey(message.getReservationKey())
                .orElseThrow();
        reservation.paid();
    }
}
