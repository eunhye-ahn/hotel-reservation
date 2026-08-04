package com.hotel.reservation.scheduler;

import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BeforeUseReservationCompleteScheduler {
    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void completeReservations(){
        int updatedCount = reservationRepository.markCompletedAfterCheckOut(
                ReservationStatus.BEFORE_USE,
                ReservationStatus.AFTER_USE,
                LocalDate.now()
        );

        log.info("이용완료 처리된 예약 건수 : {}",updatedCount);
    }
}
