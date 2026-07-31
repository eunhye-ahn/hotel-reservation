package com.hotel.payment.repository;

import com.hotel.payment.domain.Wallet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findBySellerAccount(String sellerAccount);

    interface PendingBalanceSummary {
        long getTotalPendingBalance();
        int getPendingHotelCount();
    }

    @Query("SELECT COALESCE(SUM(w.balance),0) AS totalPendingBalance, " +
            "COUNT(w) AS pendingHotelCount " +
            "FROM Wallet w")
    PendingBalanceSummary getPendingBalanceSummary();


    //미정산 top 5 리스트

    interface HotelPendingBalance  {
        long getHotelId();
        String getHotelName();
        long getTotalPendingBalance();
    }

    @Query("SELECT h.id AS hotelId, h.name AS hotelName, " +
            "COALESCE(SUM(w.balance),0) AS totalPendingBalance " +
            "FROM Wallet w " +
            "LEFT JOIN Hotel h ON h.sellerAccount = w.sellerAccount " +
            "WHERE w.balance > 0 " +
            "GROUP BY h.id, h.name " +
            "ORDER BY SUM(w.balance) DESC")
    List<HotelPendingBalance> getTopPendingHotels(Pageable pageable);
}
