package com.hotel.payment.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.payment.domain.*;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.WalletRepository;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentProcessService {

    private final LedgerRepository ledgerRepository;
    private final WalletRepository walletRepository;
    private final ReservationRepository reservationRepository;

    //웹훅
    @Transactional
    public void processDone(String orderId, String paymentKey,
                            PaymentOrder paymentOrder, PaymentEvent paymentEvent) {
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

        //wallet 업데이트 => 임시적으로 인서트허용
        Wallet wallet = walletRepository.findBySellerAccount(paymentOrder.getSellerAccount())
                .orElseGet(()-> walletRepository.save(
                        Wallet.builder()
                                .sellerAccount(paymentOrder.getSellerAccount())
                                .build()
                ));
        wallet.updateBalance(paymentOrder.getAmount());

        paymentOrder.completedLedgerAndWalletUpdate();
        paymentOrder.success();
        paymentEvent.complete(paymentKey);

        Reservation reservation = reservationRepository.findByReservationKey(paymentEvent.getReservationKey())
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        //예약 완료
        reservation.paid();
    }
}
