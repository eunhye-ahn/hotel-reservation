package com.hotel.payment.repository;

import com.hotel.payment.domain.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    Optional<PaymentOrder> findByCheckoutId(String checkoutId);
}
