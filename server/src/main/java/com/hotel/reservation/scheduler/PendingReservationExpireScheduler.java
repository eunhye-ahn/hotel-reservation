package com.hotel.reservation.scheduler;

import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.process.ReservationExpireProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [WHAT] 예약생성 후 10분이상 결제완료되지 않은 건 재고 반환 스케줄러
 *
 * scheduler : 트리거
 * service : 실제 재고반환 처리
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PendingReservationExpireScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationExpireProcessor expireProcessor;

    @Scheduled(fixedDelay = 60_000)
    public void expirePendingReservations(){
        LocalDateTime limitTime = LocalDateTime.now().minusMinutes(10);

        List<Reservation> expiredReservations = reservationRepository.findByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING,limitTime);

        for(Reservation reservation : expiredReservations){
            try{
                expireProcessor.expireReservation(reservation.getReservationKey());
            }catch (Exception e){
                log.error("예약 만료 실패 : {}",reservation.getReservationKey());
            }
        }
    }

    //재고락시, 재시도
    private void expireWithRetry(String reservationKey){
        int attempt = 0;

    }
}
