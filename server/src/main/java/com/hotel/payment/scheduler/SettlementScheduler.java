package com.hotel.payment.scheduler;

import com.hotel.payment.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {
    private final SettlementService settlementService;

    @Scheduled(cron = "0 0 3 * * *")
    public void dailySettlement() {
        log.info("정산 배치 시작");
        try{
            settlementService.dailySettlement();
            log.info("정산 배치 완료");
        }catch (Exception e){
            log.error("정산배치 실패", e);
            //알림 
        }
    }
}
