package com.hotel.reservation;

import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.RoomTypeInventory;
import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExecutorService :스레드 풀 관리자
 *  -> newFixedThreadPool(10) → 스레드 10개 미리 만들어둠
 * CountDownLatch :모든 스레드가 끝날 때까지 기다리는 장치
 *  -> new CountDownLatch(10) → 카운터 10으로 시작
 */
@Slf4j
@SpringBootTest
public class ReservationConcurrencyTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomTypeInventoryRepository roomTypeInventoryRepository;

    @Test
    void 낙관적락_동시성_테스트() throws InterruptedException {
        int threadCount = 10; // 동시 요청 10개
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            executor.submit(()->{
                try{
                    ReservationRequest request = ReservationRequest
                            .builder()
                            .reservationKey(UUID.randomUUID().toString())   //각 요청마다 다른키
                            .hotelId(1L)
                            .roomTypeId(1L)
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(1))
                            .numberOfRoomsToReserve(2)
                            .build();

                    reservationService.createReservation(request, 1L);
                }catch(Exception e){
                    log.info("충돌 발생: {}", e.getMessage());
                }finally{
                    latch.countDown(); //카운터 감소
                }
            });
        }

        latch.await(); //10개전부끝날때까지 대기
        executor.shutdown();

        //결과검증
        List<Reservation> reservations = reservationRepository.findAll();
        log.info("생성된 예약 수: {}", reservations.size());
        assertThat(reservations.size()).isEqualTo(1);

        // roomType id로 직접 조회
        RoomTypeInventory inventory = roomTypeInventoryRepository
                .findByRoomTypeIdAndDate(1L, LocalDate.now())
                .orElseThrow();

        log.info("총 재고: {}", inventory.getTotalInventory());
        log.info("예약된 수: {}", inventory.getTotalReserved());
        log.info("남은 재고: {}", inventory.getAvailableCount());
        assertThat(inventory.getTotalReserved()).isEqualTo(5);
        assertThat(inventory.getAvailableCount()).isEqualTo(5);
    }
}
