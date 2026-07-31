package com.hotel.payment.repository;

import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.reservation.repository.ReservationRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String>, PaymentOrderRepositoryCustom {
    Optional<PaymentOrder> findByCheckoutId(String checkoutId);

    interface TodayPaymentSummary {
        long getTotalAmount();

        int getCount();
    }

    @Query("SELECT COALESCE(SUM(p.amount),0) AS totalAmount, COUNT(p) AS count " +
            "FROM PaymentOrder p " +
            "WHERE p.paymentOrderStatus = :status AND p.createdAt >= :start AND p.createdAt < :end")
    TodayPaymentSummary getTodayPaymentSummary(
            @Param("status") PaymentOrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(p) FROM PaymentOrder p " +
            "WHERE p.paymentOrderStatus = :status AND p.createdAt >= :after")
    int countRecentFailed(@Param("status") PaymentOrderStatus status, @Param("after") LocalDateTime after);

    interface PaymentStatusCount {
        PaymentOrderStatus getStatus();
        int getCount();
    }

    @Query("SELECT p.paymentOrderStatus AS status, COUNT(p) AS count " +
            "FROM PaymentOrder p " +
            "WHERE p.createdAt >= :start AND p.createdAt < :end " +
            "GROUP BY p.paymentOrderStatus")
    List<PaymentStatusCount> countByStatusMonth(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);
}