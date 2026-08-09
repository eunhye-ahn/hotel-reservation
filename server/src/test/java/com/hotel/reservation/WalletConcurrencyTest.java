package com.hotel.reservation;

import com.hotel.payment.domain.Wallet;
import com.hotel.payment.repository.WalletRepository;
import com.hotel.payment.service.WalletProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "DB_PASSWORD=asd798852!",
        "JWT_SECRET=53c525c46324d79276f8bec1a5bef250"
})
public class WalletConcurrencyTest {
    @Autowired
    private WalletProcessor walletProcessor;

    @Autowired
    private WalletRepository walletRepository;

    private static final String SELLER_ACCOUNT = "11111";

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        walletRepository.save(Wallet.builder()
                .sellerAccount(SELLER_ACCOUNT)
                .build()); // balance 0에서 시작
    }

    @Test
    void 동시_결제_지갑누적() throws InterruptedException {
        int threadCount = 3;
        int amountPerPayment = 10000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    walletProcessor.updateWalletBalance(SELLER_ACCOUNT, amountPerPayment);
                } catch (Exception e) {
                    log.info("충돌 발생: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Wallet wallet = walletRepository.findBySellerAccount(SELLER_ACCOUNT).orElseThrow();
        log.info("최종 잔액: {}", wallet.getBalance());

        // 10개 요청 모두 반영되면 정확히 100000이어야 함 (유실 없음)
        assertThat(wallet.getBalance()).isEqualTo(threadCount * amountPerPayment);
    }
}
