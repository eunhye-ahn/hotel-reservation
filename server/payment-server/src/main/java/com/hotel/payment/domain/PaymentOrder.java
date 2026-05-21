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

<<<<<<< Updated upstream
=======
    @Builder.Default
    private int retryCount = 0;

    public static final int MAX_RETRY_COUNT = 3;

    public boolean isRetryExhausted(){
        return this.retryCount >= MAX_RETRY_COUNT;
    }
    public void incrementRetryCount(){
        this.retryCount += 1;
    }

    public void completedLedgerAndWalletUpdate(){
        this.ledgerUpdated = true;
        this.walletUpdated = true;
    }
>>>>>>> Stashed changes
    public void success(){
        this.paymentOrderStatus = PaymentOrderStatus.SUCCESS;
    }
    public void fail() {
        this.paymentOrderStatus = PaymentOrderStatus.FAILED;
    }
}
