package com.hotel.reservation.domain.payment;

import com.hotel.reservation.domain.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *   RETRY_QUEUE {
 *     bigserial id PK
 *     varchar payment_order_id FK
 *     int retry_count
 *     timestamp next_retry_at
 *     varchar status
 *     timestamp created_at
 *   }
 */
@Table(name="retry_queue")
@Entity
@Getter
@NoArgsConstructor
public class RetryQueue extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private LocalDateTime nextRetryAt;

    @Enumerated(EnumType.STRING)
    private RetryStatus retryStatus;
}
