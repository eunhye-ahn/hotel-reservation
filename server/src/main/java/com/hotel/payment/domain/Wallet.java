package com.hotel.payment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *   WALLET {
 *     bigserial id PK
 *     varchar account UK
 *     int balance
 *     timestamp updated_at
 *   }
 */
@Table(name="wallet")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Wallet extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sellerAccount;

    @Builder.Default
    @Column(nullable = false)
    private int balance = 0;

    //update only
    public void updateBalance(int amount){
        this.balance += amount;
    }
}
