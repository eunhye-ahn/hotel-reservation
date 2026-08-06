package com.hotel.reservation;

import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.process.ReservationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ExecutorService :스레드 풀 관리자
 *  -> newFixedThreadPool(10) → 스레드 10개 미리 만들어둠
 * CountDownLatch :모든 스레드가 끝날 때까지 기다리는 장치
 *  -> new CountDownLatch(10) → 카운터 10으로 시작
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "DB_PASSWORD=asd798852!",
        "JWT_SECRET=53c525c46324d79276f8bec1a5bef250"
})
public class ReservationConcurrencyTest {
    @Autowired
    private ReservationProcessor reservationProcessor;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomTypeInventoryRepository roomTypeInventoryRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
    }

    @Test
    void 동시_요청_충돌_재고_정합성_유지() throws InterruptedException {
        int threadCount = 10; // 동시 요청 10개
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            executor.submit(()->{
                try{
                    ReservationRequest request = new ReservationRequest(
                            UUID.randomUUID().toString(),   //각 요청마다 다른키
                            1L,
                            1L,
                            LocalDate.now(),
                            LocalDate.now().plusDays(1),
                            2,
                            2
                    );

                    reservationProcessor.processWithRetry(request, 1L);
                }catch(Exception e){
                    log.info("충돌 발생: {}", e.getMessage());
                }finally{
                    latch.countDown(); //카운터 감소
                }
            });
        }

        latch.await(); //10개전부끝날때까지 대기
        executor.shutdown();

        //결과검증 -재고정합성
        List<Reservation> reservations = reservationRepository.findAll();
        log.info("생성된 예약 수: {}", reservations.size());

        RoomTypeInventory inventory = roomTypeInventoryRepository
                .findByRoomTypeIdAndDate(1L, LocalDate.now())
                .orElseThrow();

        log.info("총 재고: {}", inventory.getTotalInventory());
        log.info("예약된 수: {}", inventory.getTotalReserved());
        log.info("남은 재고: {}", inventory.getAvailableCount());

        assertThat(inventory.getTotalReserved()).isLessThanOrEqualTo(
                (int)(inventory.getTotalInventory() * 1.1)
        );
    }
}
