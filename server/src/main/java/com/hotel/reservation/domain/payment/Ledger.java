package com.hotel.reservation.domain.payment;

import com.hotel.reservation.domain.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *   LEDGER {
 *     bigserial id PK
 *     varchar payment_order_id FK
 *     varchar account
 *     varchar account_type
 *     varchar debit
 *     varchar credit
 *     timestamp created_at
 *   }
 */
@Table(name="ledger")
@Entity
@Getter
@NoArgsConstructor
public class Ledger extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="payment_order_id", nullable = false)
    private String paymentOrderId;

    @Column(nullable = false)
    private String account;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    private Integer debit;  //차감

    @Column(nullable = false)
    private Integer credit; //증가
}
