package com.hotel.payment.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.payment.domain.AccountType;
import com.hotel.payment.domain.Wallet;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;
    private final HotelRepository hotelRepository;
    private final SettlementTransactionService settlementTransactionService;

    public void executeSettlementByAdmin(Long hotelId, LocalDate periodStart, LocalDate periodEnd){
        List<Wallet> wallets;

        if(hotelId != null){
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));
            Wallet wallet = walletRepository.findBySellerAccount(hotel.getSellerAccount())
                    .orElseThrow(()->new CustomException(ErrorCode.WALLET_NOT_FOUND));
            wallets = List.of(wallet);
        }else{
            wallets = walletRepository.findAll();
        }

        for(Wallet wallet : wallets){
            settleWallet(wallet, periodStart, periodEnd);
        }
    }

    public void dailySettlement(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Wallet> targets = walletRepository.findAllByBalanceGreaterThan(0);
        for (Wallet wallet : targets) {
            settleWallet(wallet, yesterday, yesterday);
        }
    }

    private void settleWallet(Wallet wallet, LocalDate periodStart, LocalDate periodEnd) {
        int periodAmount = ledgerRepository.calculateSettlementAmount(
                wallet.getSellerAccount(),
                AccountType.SELLER,
                periodStart.atTime(0, 0, 0),
                periodEnd.atTime(23, 59, 59)
        );

        if (periodAmount <= 0) {
            return;
        }

        Long settlementId = settlementTransactionService.createPendingSettlement(
                wallet.getSellerAccount(),
                periodAmount,
                periodStart,
                periodEnd
        );

        try {
            settlementTransactionService.completedSettlement(settlementId, wallet.getSellerAccount(), periodAmount);
        } catch (Exception e) {
            settlementTransactionService.failedSettlement(settlementId);
        }
    }
}
