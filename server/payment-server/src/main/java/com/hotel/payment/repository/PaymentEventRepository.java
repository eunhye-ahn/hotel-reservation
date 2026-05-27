package com.hotel.payment.repository;

import com.hotel.payment.domain.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, String> {
    Optional<PaymentEvent> findByCheckoutId(String checkoutId);

    Optional<PaymentEvent> findByReservationId(Long reservationId);

    Optional<PaymentEvent> findByOrderId(String orderId);
}
