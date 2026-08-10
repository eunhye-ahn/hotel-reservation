package com.hotel.payment.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.payment.client.TossPaymentClient;
import com.hotel.payment.domain.*;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.repository.WalletRepository;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentTransactionService {

    private final LedgerRepository ledgerRepository;
    private final WalletProcessor walletProcessor;
    private final ReservationRepository reservationRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final WalletRepository walletRepository;

    //웹훅
    @Transactional
    public void processDone(String orderId, String paymentKey,
                            PaymentOrder paymentOrder, PaymentEvent paymentEvent) {
        //중복 웹훅 방어
        if(paymentOrder.getPaymentOrderStatus() == PaymentOrderStatus.SUCCESS){
            return;
        }

        //판매자
        ledgerRepository.save(Ledger.builder()
                .paymentOrderId(orderId)
                .account(paymentOrder.getSellerAccount())
                .accountType(AccountType.SELLER)
                .debit(null)
                .credit(paymentOrder.getAmount())
                .build()
        );
        //구매자
        ledgerRepository.save(Ledger.builder()
                .paymentOrderId(orderId)
                .account(paymentEvent.getUserId().toString())
                .accountType(AccountType.BUYER)
                .debit(paymentOrder.getAmount())
                .credit(null)
                .build()
        );

        //wallet 업데이트 => 임시적으로 인서트허용
        walletProcessor.updateWalletBalance(paymentOrder.getSellerAccount(), paymentOrder.getAmount());

        paymentOrder.completedLedgerAndWalletUpdate();
        paymentOrder.success();
        paymentEvent.registerPspToken(paymentKey);

        Reservation reservation = reservationRepository.findByReservationKey(paymentEvent.getReservationKey())
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        //예약 확정 및 결제완료
        reservation.paid();
    }

    //결제취소 상태 반영 트랜잭션
    @Transactional
    public void confirmPaymentCancellation(Reservation reservation){
        PaymentEvent paymentEvent = paymentEventRepository.findByReservationId(reservation.getId())
                .orElseThrow(()->new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        PaymentOrder paymentOrder = paymentOrderRepository.findByCheckoutId(paymentEvent.getCheckoutId())
                .orElseThrow(()->new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        String paymentOrderId = paymentOrder.getPaymentOrderId();
        int amount = reservation.getTotalPrice();

        ledgerRepository.save(Ledger.builder()
                .paymentOrderId(paymentOrderId)
                .account(reservation.getUser().getId().toString())
                .accountType(AccountType.BUYER)
                .debit(0)
                .credit(amount)
                .build());
        ledgerRepository.save(Ledger.builder()
                .paymentOrderId(paymentOrderId)
                .account(reservation.getHotel().getSellerAccount())
                .accountType(AccountType.SELLER)
                .debit(amount)
                .credit(0)
                .build());

        Wallet wallet = walletRepository.findBySellerAccount(reservation.getHotel().getSellerAccount())
                .orElseThrow(()->new CustomException(ErrorCode.WALLET_NOT_FOUND));
        wallet.updateBalance(-amount);

        paymentOrder.cancel();
        reservation.refunded();
    }

    //취소확정 트랜잭션
    @Transactional
    public void completeCancelStatus(Reservation reservation){
        reservation.completeCancel();
    }
}
