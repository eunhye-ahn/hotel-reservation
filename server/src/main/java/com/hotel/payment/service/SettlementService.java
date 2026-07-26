package com.hotel.payment.service;

import com.hotel.admin.dto.payment.AdminSettlementSearchResponse;
import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.payment.domain.AccountType;
import com.hotel.payment.domain.Settlement;
import com.hotel.payment.domain.SettlementSearchType;
import com.hotel.payment.domain.Wallet;
import com.hotel.payment.mapper.SettlementMapper;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.SettlementRepository;
import com.hotel.payment.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final WalletRepository walletRepository;
    private final LedgerRepository ledgerRepository;
    private final HotelRepository hotelRepository;
    private final SettlementMapper mapper;

    @Transactional
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
            int periodAmount = ledgerRepository.calculateSettlementAmount(
                    wallet.getSellerAccount(),
                    AccountType.SELLER,
                    periodStart.atTime(0,0,0),
                    periodEnd.atTime(23,59,59)
            );

            if(periodAmount <= 0){
                continue;
            }

            //정산시작
            Settlement settlement = Settlement.builder()
                    .sellerAccount(wallet.getSellerAccount())
                    .amount(periodAmount)
                    .periodStartDate(periodStart)
                    .periodEndDate(periodEnd)
                    .build();

            settlementRepository.save(settlement);

            //실제 지급대행 API - 지금은 상태변경으로 임시처리
            settlement.complete();

            //정산완료 -> 지갑 잔액 리셋
            wallet.updateBalance(-periodAmount);
        }
    }
}
