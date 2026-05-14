package com.hotel.payment.domain;

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
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentOrderId;

    @Column(nullable = false)
    private String account;

    @Enumerated(EnumType.STRING)
    private AccountType accountType; // BUYER, SELLER

    private Integer debit;  //차감

    private Integer credit; //증가
}
