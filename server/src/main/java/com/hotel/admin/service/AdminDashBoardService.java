package com.hotel.admin.service;

import com.hotel.admin.dto.dashboard.DashBoardSummaryResponse;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.repository.WalletRepository;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AdminDashBoardService {
    private final ReservationRepository reservationRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final WalletRepository walletRepository;

    public DashBoardSummaryResponse getDashBoardSummaryInfo() {
        LocalDate today =  LocalDate.now();
        LocalDateTime start = today.atTime(0,0,0);
        LocalDateTime end = today.atTime(23,59,59);

        int todayCheckInCount = reservationRepository.countTodayCheckIn(today, ReservationStatus.BEFORE_USE);
        int unassignedCount = reservationRepository.countUnassigned(ReservationStatus.BEFORE_USE);
        long todayPaymentAmount = paymentOrderRepository.sumTodayAmount(PaymentOrderStatus.SUCCESS, start, end);
        long totalPendingBalance = walletRepository.sumAllBalance();
        int failedPaymentCount = paymentOrderRepository.countRecentFailed(PaymentOrderStatus.FAILED,LocalDateTime.now().minusHours(24));

        return DashBoardSummaryResponse.builder()
                .todayCheckInCount(todayCheckInCount)
                .unassignedCount(unassignedCount)
                .todayPaymentAmount(todayPaymentAmount)
                .totalPendingBalance(totalPendingBalance)
                .failedPaymentCount(failedPaymentCount)
                .build();
    }
}
