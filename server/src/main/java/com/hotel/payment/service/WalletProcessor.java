package com.hotel.payment.service;

import com.hotel.payment.domain.Wallet;
import com.hotel.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletProcessor {

    private final WalletRepository walletRepository;

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void updateWalletBalance(String sellerAccount, int amount) {
        Wallet wallet = walletRepository.findBySellerAccount(sellerAccount)
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder().sellerAccount(sellerAccount).build()
                ));
        wallet.updateBalance(amount);
    }

    @Recover
    public void recover(ObjectOptimisticLockingFailureException e, String sellerAccount, int amount) {
        log.error("Wallet 갱신 재시도 모두 실패 - sellerAccount: {}, amount: {}", sellerAccount, amount);
        throw e; // 웹훅 처리 실패로 전파 -> PSP가 웹훅 재전송하도록 유도
    }
}