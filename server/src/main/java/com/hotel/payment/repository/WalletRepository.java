package com.hotel.payment.repository;

import com.hotel.payment.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findBySellerAccount(String sellerAccount);

    @Query("SELECT SUM(COALESCE(w.balance,0)) FROM Wallet w")
    long sumAllBalance();
}
