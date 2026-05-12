package com.hotel.reservation;

import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.RoomTypeInventory;
import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.service.PriceTokenRedisService;
import com.hotel.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Autowired
    private PriceTokenRedisService priceTokenRedisService;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
    }

    @Test
    void 단일_예약_성공(){
        String token = priceTokenRedisService.save(100000);

        ReservationRequest request = ReservationRequest.builder()
                .reservationKey(UUID.randomUUID().toString())
                .hotelId(1L)
                .roomTypeId(1L)
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .numberOfRooms(2)
                .numberOfGuests(2)
                .priceToken(token)
                .build();

        reservationService.createReservation(request, 1L);

        // 결과 검증
        RoomTypeInventory inventory = roomTypeInventoryRepository
                .findByRoomTypeIdAndDate(1L, LocalDate.now().plusDays(1))
                .orElseThrow();

        assertThat(inventory.getVersion()).isEqualTo(1L); // N+1
        assertThat(inventory.getTotalReserved()).isEqualTo(2);
    }

    @Test
    void 동시_요청_충돌_재고_정합성_유지() throws InterruptedException {
        int threadCount = 10; // 동시 요청 10개
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            String token = priceTokenRedisService.save(100000);
            executor.submit(()->{
                try{
                    ReservationRequest request = ReservationRequest
                            .builder()
                            .reservationKey(UUID.randomUUID().toString())   //각 요청마다 다른키
                            .hotelId(1L)
                            .roomTypeId(1L)
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(1))
                            .numberOfRooms(2)
                            .numberOfGuests(2)
                            .priceToken(token)
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

    @Test
    void 잔여_객실_부족(){
        String token = priceTokenRedisService.save(100000);
        //totalReserved = totalInventory인 데이터(10프로 초과예약 고려)
        ReservationRequest request = ReservationRequest.builder()
                .reservationKey(UUID.randomUUID().toString())
                .hotelId(1L)
                .roomTypeId(2L)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .numberOfRooms(1)
                .numberOfGuests(2)
                .priceToken(token)
                .build();

        //결과 검증 -db변경없는지
        RoomTypeInventory inventory = roomTypeInventoryRepository
                .findByRoomTypeIdAndDate(2L, LocalDate.now())
                        .orElseThrow();
        assertThat(inventory.getTotalReserved()).isEqualTo(11);

        assertThatThrownBy(()-> reservationService.createReservation(request,1L))
                .isInstanceOf(CustomException.class);
        //assertThatThrownBy(()-> reservationService.createReservation(request,1L))
        //        .isInstanceOf(CustomException.class)
        //        .satisfies(e -> {
        //            CustomException ce = (CustomException) e;
        //            assertThat(ce.getErrorCode()).isEqualTo(ErrorCode.RESERVATION_UNAVAILABLE);
        //        });

    }

    @Test
    void 재시도_정책() throws InterruptedException {
        int threadCount = 10; // 동시 요청 10개
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            String token = priceTokenRedisService.save(100000);
            executor.submit(()->{
                try{
                    ReservationRequest request = ReservationRequest
                            .builder()
                            .reservationKey(UUID.randomUUID().toString())   //각 요청마다 다른키
                            .hotelId(2L)
                            .roomTypeId(3L)
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(1))
                            .numberOfRooms(2)
                            .numberOfGuests(2)
                            .priceToken(token)
                            .build();

                    reservationService.createReservation(request, 1L);
                }catch(Exception e){
                    log.info("충돌 발생: {}", e.getMessage());
                }finally{
                    latch.countDown(); //카운터 감소
                }
            });
        }

        latch.await();
        executor.shutdown();

        //결과검증
        List<Reservation> reservations = reservationRepository.findAll();
        log.info("생성된 예약 수: {}", reservations.size());
        assertThat(reservations.size()).isGreaterThan(1);

        RoomTypeInventory inventory = roomTypeInventoryRepository
                .findByRoomTypeIdAndDate(3L, LocalDate.now())
                .orElseThrow();

        assertThat(inventory.getTotalReserved()).isLessThanOrEqualTo(
                (int)(inventory.getTotalInventory() * 1.1)
        );

        log.info("총 재고: {}", inventory.getTotalInventory());
        log.info("예약된 수: {}", inventory.getTotalReserved());
        log.info("남은 재고: {}", inventory.getAvailableCount());
    }

    @Test
    void 경계_조건() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            String token = priceTokenRedisService.save(100000);
            executor.submit(()->{
                try{
                    ReservationRequest request = ReservationRequest
                            .builder()
                            .reservationKey(UUID.randomUUID().toString())   //각 요청마다 다른키
                            .hotelId(3L)
                            .roomTypeId(4L)
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(1))
                            .numberOfRooms(1)
                            .numberOfGuests(2)
                            .priceToken(token)
                            .build();

                    reservationService.createReservation(request, 1L);
                }catch(Exception e){
                    log.info("충돌 발생: {}", e.getMessage());
                }finally{
                    latch.countDown(); //카운터 감소
                }
            });
        }

        latch.await();
        executor.shutdown();


        //결과검증
        List<Reservation> reservations = reservationRepository.findAll();
        log.info("생성된 예약 수: {}", reservations.size());
        assertThat(reservations.size()).isEqualTo(1);

        RoomTypeInventory inventory = roomTypeInventoryRepository
                .findByRoomTypeIdAndDate(4L, LocalDate.now())
                .orElseThrow();

        assertThat(inventory.getTotalReserved()).isEqualTo(5);
    }
}
