package com.hotel.admin.service;

import com.hotel.admin.dto.dashboard.*;
import com.hotel.admin.mapper.StatisticMapper;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.repository.WalletRepository;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashBoardService {
    private final ReservationRepository reservationRepository;
    private final PaymentOrderRepository paymentOrderRepository;
    private final WalletRepository walletRepository;
    private final StatisticMapper statisticMapper;

    public DashBoardSummaryResponse getDashBoardSummaryInfo() {
        LocalDate today =  LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime start = today.atTime(0,0,0);
        LocalDateTime end = today.atTime(23,59,59);

        int todayCheckInCount = reservationRepository.countCheckInByDate(today, ReservationStatus.BEFORE_USE);
        int yesterdayCheckInCount = reservationRepository.countCheckInByDate(yesterday, ReservationStatus.BEFORE_USE);
        int unassignedCount = reservationRepository.countUnassigned(ReservationStatus.BEFORE_USE);
        PaymentOrderRepository.TodayPaymentSummary paymentSummary = paymentOrderRepository.getTodayPaymentSummary(PaymentOrderStatus.SUCCESS, start, end);
        WalletRepository.PendingBalanceSummary pendingBalanceSummary = walletRepository.getPendingBalanceSummary();
        int failedPaymentCount = paymentOrderRepository.countRecentFailed(PaymentOrderStatus.FAILED,LocalDateTime.now().minusHours(24));

        return DashBoardSummaryResponse.builder()
                .todayCheckInCount(todayCheckInCount)
                .checkInDiff(todayCheckInCount - yesterdayCheckInCount)
                .unassignedCount(unassignedCount)
                .todayPaymentAmount(paymentSummary.getTotalAmount())
                .todayPaymentCount(paymentSummary.getCount())
                .totalPendingBalance(pendingBalanceSummary.getTotalPendingBalance())
                .pendingHotelCount(pendingBalanceSummary.getPendingHotelCount())
                .failedPaymentCount(failedPaymentCount)
                .build();
    }

    public List<DailyStatisticsResponse> getDailyStatisticsInfo() {
        return statisticMapper.getDailyStatistics();
    }

    public List<UnassignRoomInfo> unAssignReservationInfo(){
        Pageable pageable = PageRequest.of(0, 5);
        List<Reservation> unassignReservation = reservationRepository.findUnassignedPreview(ReservationStatus.BEFORE_USE, pageable);

        return unassignReservation.stream()
                .map(UnassignRoomInfo::from)
                .toList();
    }

    public List<ReserveStatusStaticResponse> getReserveStatusByMonth(){
        LocalDateTime end =  LocalDate.now().atTime(23,59,59);
        LocalDateTime start = end.minusMonths(1);

        return reservationRepository.countByStatusMonth(start, end)
                .stream()
                .map(r-> new ReserveStatusStaticResponse(r.getStatus(), r.getCount()))
                .toList();
    }

    public List<PaymentStatusStaticResponse>  getPaymentStatusByMonth(){
        LocalDateTime end =  LocalDate.now().atTime(23,59,59);
        LocalDateTime start = end.minusMonths(1);

        return paymentOrderRepository.countByStatusMonth(start, end)
                .stream().map(p->new PaymentStatusStaticResponse(p.getStatus(), p.getCount()))
                .toList();
    }

    public List<TopPendingBalanceHotel> getTopPendingHotels(){
        Pageable pageable = PageRequest.of(0, 5);
        return walletRepository.getTopPendingHotels(pageable)
                .stream()
                .map(w-> new TopPendingBalanceHotel(
                        w.getHotelId(),
                        w.getHotelName(),
                        w.getTotalPendingBalance()
                ))
                .toList();
    }
}
