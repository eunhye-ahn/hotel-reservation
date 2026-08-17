package com.hotel.payment.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.common.idempotency.IdempotencyRedisService;
import com.hotel.payment.domain.AccountType;
import com.hotel.payment.domain.Ledger;
import com.hotel.payment.domain.Settlement;
import com.hotel.payment.domain.Wallet;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.SettlementRepository;
import com.hotel.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SettlementTransactionService {
    private final SettlementRepository settlementRepository;
    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;

    @Transactional
    public Long createPendingSettlement (String sellerAccount, int amount, LocalDate periodStart, LocalDate periodEnd, String settlementKey) {
        //정산시작 => Pending 저장
        Settlement settlement = Settlement.builder()
                .sellerAccount(sellerAccount)
                .amount(amount)
                .periodStartDate(periodStart)
                .periodEndDate(periodEnd)
                .settlementKey(settlementKey)
                .build();

        return settlementRepository.save(settlement).getId();
    }

    @Transactional
    public void completedSettlement (Long settlementId, String sellerAccount, int amount) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));
        settlement.complete();
        Wallet wallet = walletRepository.findBySellerAccount(sellerAccount)
                        .orElseThrow(() -> new CustomException(ErrorCode.WALLET_NOT_FOUND));
        wallet.updateBalance(-amount);

        ledgerRepository.save(Ledger.builder()
                        .account(sellerAccount)
                        .accountType(AccountType.SELLER)
                        .debit(amount)
                        .credit(null)
                .build());
        ledgerRepository.save(Ledger.builder()
                .account("PLATFORM")
                .accountType(AccountType.PLATFORM)
                .debit(null)
                .credit(amount)
                .build());
    }

    @Transactional
    public void failedSettlement (Long settlementId){
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));
        settlement.fail();
    }

    @Transactional
    public void markNeedsReconciliation(Long settlementId){
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.SETTLEMENT_NOT_FOUND));
        settlement.needsReconciliation();
    }
}
