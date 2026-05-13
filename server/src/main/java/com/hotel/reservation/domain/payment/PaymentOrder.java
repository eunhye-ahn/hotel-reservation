package com.hotel.reservation.domain.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="payment_order")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentOrder {
    @Id
    private String paymentOrderId;

    @Column(name="checkout_id", nullable = false)
    private String checkoutId;

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    private PaymentOrderStatus paymentOrderStatus;

    @Builder.Default
    @Column(nullable = false)
    boolean ledgerUpdated = false;

    @Builder.Default
    @Column(nullable = false)
    boolean walletUpdated = false;
}
