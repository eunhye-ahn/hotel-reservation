package com.hotel.reservation.domain.payment;

import com.hotel.reservation.domain.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="dead_letter_queue")
@Entity
@Getter
@NoArgsConstructor
public class DeadLetterQueue extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="payment_order_id", nullable = false)
    private String paymentOrderId;

    @Column(name="failed_reason", nullable = false)
    private String failedReason;
}
