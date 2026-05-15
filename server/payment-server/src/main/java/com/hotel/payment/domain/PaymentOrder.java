package com.hotel.payment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="payment_order",
    indexes = @Index(name="IDX_payment_order_status", columnList = "payment_order_status")
)
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentOrder extends BaseTime{
    @Id
    private String paymentOrderId;

    @Column(nullable = false)
    private String checkoutId;

    @Column(nullable = false)
    private String sellerAccount;

    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentOrderStatus paymentOrderStatus = PaymentOrderStatus.NOT_STARTED;

    @Builder.Default
    private boolean ledgerUpdated = false;

    @Builder.Default
    private boolean walletUpdated = false;

    public void success(){
        this.paymentOrderStatus = PaymentOrderStatus.SUCCESS;
    }
    public void fail() {
        this.paymentOrderStatus = PaymentOrderStatus.FAILED;
    }
    public void executing(){
        this.paymentOrderStatus = PaymentOrderStatus.EXECUTING;
    }
}
