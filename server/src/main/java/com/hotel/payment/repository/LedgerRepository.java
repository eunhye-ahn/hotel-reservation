package com.hotel.payment.repository;

import com.hotel.payment.domain.AccountType;
import com.hotel.payment.domain.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    Optional<Ledger> findByPaymentOrderId(String paymentOrderId);

    @Query("SELECT COALESCE(SUM(credit),0) - COALESCE(SUM(debit), 0) " +
            "FROM Ledger " +
            "WHERE account = :sellerAccount " +
            "AND accountType = :accountType " +
            "AND createdAt BETWEEN :start AND :end")
    int calculateSettlementAmount(@Param("sellerAccount") String sellerAccount,
                                              @Param("accountType") AccountType accountType,
                                              @Param("start") LocalDateTime start,
                                              @Param("end")LocalDateTime end);
}
