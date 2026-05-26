package com.hotel.payment.service;

import com.hotel.payment.domain.*;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentProcessService {

    private final LedgerRepository ledgerRepository;
    private final WalletRepository walletRepository;

    //웹훅
    @Transactional
    public void processDone(String orderId, String paymentKey,
                            PaymentOrder paymentOrder, PaymentEvent paymentEvent){
        //판매자
        ledgerRepository.save(Ledger.builder()
                .paymentOrderId(orderId)
                .account(paymentOrder.getSellerAccount())
                .accountType(AccountType.SELLER)
                .debit(paymentOrder.getAmount())
                .credit(null)
                .build()
        );
        //구매자
        ledgerRepository.save(Ledger.builder()
                .paymentOrderId(orderId)
                .account(paymentEvent.getUserId().toString())
                .accountType(AccountType.BUYER)
                .debit(null)
                .credit(paymentOrder.getAmount())
                .build()
        );

        //wallet 업데이트
        Wallet wallet = walletRepository.findBySellerAccount(paymentOrder.getSellerAccount())
                .orElseThrow();//updateonly로할까?
        wallet.updateBalance(paymentOrder.getAmount());

        paymentOrder.completedLedgerAndWalletUpdate();
        paymentOrder.success();
        paymentEvent.complete(paymentKey);
    }
}
