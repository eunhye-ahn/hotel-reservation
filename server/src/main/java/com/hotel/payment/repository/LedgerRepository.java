package com.hotel.payment.repository;

import com.hotel.payment.domain.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    Optional<Ledger> findByPaymentOrderId(String paymentOrderId);
}
