package com.hotel.payment.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * [WHAT] 미처리 결제 건 주기적 확인
 * [WHY] 웹훅 실패, 트랜잭션 실패, 서버 다운 등으로 누락된 건 자동 복구
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentBatchScheduler {
    @Scheduled(fixedDelay = 300000)
    public void processUnhandledPayments() {
        //결제오더 not_started 상태면서 10분 이상 지난 건 조회

        //각 건마다 토스결제조회 api 호출

        log.info("batch-미처리 결제 건 확인 완료");
    }
}
