package com.hotel.reservation.service.process;


import com.hotel.reservation.service.ReservationTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpireProcessor {
    private final ReservationTransactionService transactionService;

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void expireReservation(String reservationKey){
        transactionService.expireReservationInTransaction(reservationKey);
    }

    @Recover
    public void expireRecover(RuntimeException e, String reservationKey){
        log.error("예약 만료 처리 재시도 모두 실패 - reservationKey: {}", reservationKey);
    }
}
