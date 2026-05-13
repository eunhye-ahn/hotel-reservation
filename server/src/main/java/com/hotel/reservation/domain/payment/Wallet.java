package com.hotel.reservation.domain.payment;

import com.hotel.reservation.domain.BaseTime;
import jakarta.persistence.*;
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
public class Wallet extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String account;

    @Column(nullable = false)
    private int balance;

    //update only
    public void updateBalance(int amount){
        this.balance += amount;
    }
}
