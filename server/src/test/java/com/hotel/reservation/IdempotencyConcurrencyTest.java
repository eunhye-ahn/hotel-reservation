package com.hotel.reservation;

import com.hotel.common.jwt.JwtProvider;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.RateRepository;
import com.hotel.payment.domain.AccountType;
import com.hotel.payment.domain.Ledger;
import com.hotel.payment.domain.Wallet;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.SettlementRepository;
import com.hotel.payment.repository.WalletRepository;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.user.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "DB_PASSWORD=asd798852!",
        "JWT_SECRET=53c525c46324d79276f8bec1a5bef250"
})
public class IdempotencyConcurrencyTest {

    private static final String JWT_SECRET = "53c525c46324d79276f8bec1a5bef250";
    private static final Long TEST_USER_ID = 1L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RateRepository rateRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    PaymentEventRepository paymentEventRepository;

    @Autowired
    SettlementRepository settlementRepository;

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    LedgerRepository ledgerRepository;


    String testToken;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        log.info("오늘 Rate 개수: {}",
                rateRepository.findByRoomTypeIdAndDateBetween(1L, LocalDate.now(), LocalDate.now()).size());
        testToken = jwtProvider.generateAccessToken(TEST_USER_ID, Role.ROLE_ADMIN);

        Hotel hotel = hotelRepository.findById(1L).orElseThrow();
        String sellerAccount = hotel.getSellerAccount();

        if (walletRepository.findBySellerAccount(sellerAccount).isEmpty()) {
            walletRepository.save(Wallet.builder()
                    .sellerAccount(sellerAccount)
                    .balance(0)
                    .build());
        }

        ledgerRepository.save(Ledger.builder()
                .account(sellerAccount)
                .accountType(AccountType.SELLER)
                .credit(100000)
                .debit(0)
                .paymentOrderId("test-order-" + UUID.randomUUID())
                .build());
    }
    @Test
    void 예약멱등키() throws Exception {
        String key = "concurrent-test-key";
        String body = """
                {"reservationKey":"%s","hotelId":1,"roomTypeId":1,
                 "startDate":"2026-08-09","endDate":"2026-08-11",
                 "numberOfGuests":2,"numberOfRooms":1}
                """.formatted(key);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Integer> statusCodes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/v1/reservations")
                                    .header("Idempotency-Key", key)
                                    .header("Authorization", "Bearer " + testToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andReturn();
                    statusCodes.add(result.getResponse().getStatus());
                } catch (Exception e) {
                    log.info("요청 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        log.info("응답 상태코드 목록: {}", statusCodes);

        long reservationCount = reservationRepository.findAll().stream()
                .filter(r -> r.getReservationKey().equals(key))
                .count();

        log.info("생성된 예약 수: {}", reservationCount);
        assertThat(reservationCount).isEqualTo(1);
    }


    @Test
    void 결제멱등키() throws Exception {
        // 1) 예약 먼저 생성 (단일 요청)
        String reservationKey = UUID.randomUUID().toString();
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(2);

        String reservationBody = """
                {"reservationKey":"%s","hotelId":1,"roomTypeId":1,
                 "startDate":"%s","endDate":"%s",
                 "numberOfGuests":2,"numberOfRooms":1}
                """.formatted(reservationKey, startDate, endDate);

        mockMvc.perform(post("/api/v1/reservations")
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationBody))
                .andReturn();

        // 2) 같은 예약에 대해 결제 준비를 10개 스레드가 동시에 요청
        String paymentIdemKey = "concurrent-payment-key";
        String paymentBody = """
                {"orderId":"%s"}
                """.formatted(UUID.randomUUID().toString());

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Integer> statusCodes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/v1/payments/prepare/" + reservationKey)
                                    .header("Idempotency-Key", paymentIdemKey)
                                    .header("Authorization", "Bearer " + testToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(paymentBody))
                            .andReturn();
                    statusCodes.add(result.getResponse().getStatus());
                    log.info("응답: status={}, body={}",
                            result.getResponse().getStatus(),
                            result.getResponse().getContentAsString());
                } catch (Exception e) {
                    log.info("요청 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        log.info("응답 상태코드 목록: {}", statusCodes);

        long paymentEventCount = paymentEventRepository.findAll().stream()
                .filter(e -> e.getReservationKey().equals(reservationKey))
                .count();

        log.info("생성된 PaymentEvent 수: {}", paymentEventCount);
        assertThat(paymentEventCount).isEqualTo(1);
    }


    @Test
    void 정산멱등키() throws Exception {
        String settlementKey = "concurrent-settlement-key";
        String body = """
                {"periodStart":"%s","periodEnd":"%s"}
                """.formatted(LocalDate.now(), LocalDate.now());

        int threadCount = 10;
        int TEST_HOTEL_ID = 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Integer> statusCodes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/v1/admin/settlement/" + TEST_HOTEL_ID + "/execute")
                                    .header("Idempotency-Key", settlementKey)
                                    .header("Authorization", "Bearer " + testToken)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andReturn();
                    statusCodes.add(result.getResponse().getStatus());
                    log.info("응답: status={}, body={}",
                            result.getResponse().getStatus(),
                            result.getResponse().getContentAsString());

                } catch (Exception e) {
                    log.info("요청 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();

                }
            });
        }
        latch.await();
        executor.shutdown();

        log.info("응답 상태코드 목록: {}", statusCodes);

        long settlementCount = settlementRepository.findAll().stream()
                .filter(s -> settlementKey.equals(s.getSettlementKey()))
                .count();

        log.info("생성된 Settlement 수: {}", settlementCount);
        assertThat(settlementCount).isEqualTo(1);
    }
}