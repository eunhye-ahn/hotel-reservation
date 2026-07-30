package com.hotel.payment.repository;

import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String>, PaymentOrderRepositoryCustom {
    Optional<PaymentOrder> findByCheckoutId(String checkoutId);

    @Query("SELECT SUM(COALESCE(p.amount,0)) FROM PaymentOrder p " +
            "WHERE p.paymentOrderStatus = :status AND p.createdAt >= :start AND p.createdAt < :end")
    long sumTodayAmount(
            @Param("status")PaymentOrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(p) FROM PaymentOrder p " +
            "WHERE p.paymentOrderStatus = :status AND p.createdAt >= :after")
    int countRecentFailed(PaymentOrderStatus status, LocalDateTime after);
}
