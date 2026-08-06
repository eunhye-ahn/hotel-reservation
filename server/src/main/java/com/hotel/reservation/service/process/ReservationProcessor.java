package com.hotel.reservation.service.process;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.reservation.dto.ReservationRequest;
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
public class ReservationProcessor {

    private final ReservationTransactionService reservationTransactionService;

    //동시성제어 - 동시예약 처리
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public void processWithRetry(ReservationRequest request, Long userId) {
        log.info("processor retry - reservationKey: {}", request.reservationKey());
        reservationTransactionService.createReservationInTransaction(request, userId);
    }

    @Recover
    public void recover(ObjectOptimisticLockingFailureException e,
                        ReservationRequest request, Long userId){
        log.error("예약 재시도 모두 실패 - reservationKey: {}",
                request.reservationKey());
        throw new CustomException(ErrorCode.RESERVATION_CONFLICT);
    }

    @Recover
    public void recover(CustomException e,
                        ReservationRequest request, Long userId) {
        log.info("예약 불가 - reservationKey: {}, 사유: {}", request.reservationKey(), e.getMessage());
        throw e; // 원래 예외(재고부족 등) 그대로 던짐
    }
}
