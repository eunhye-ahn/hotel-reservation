package com.hotel.payment.repository;

import com.hotel.payment.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findBySellerAccount(String sellerAccount);
}
