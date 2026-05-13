package com.hotel.reservation.domain.payment;

import com.hotel.reservation.domain.BaseTime;
import com.hotel.reservation.domain.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *   PAYMENT_EVENT {
 *     varchar checkout_id PK
 *     bigint reservation_id FK
 *     varchar psp_token
 *     boolean is_payment_done
 *     timestamp created_at
 *   }
 */
@Table(name="payment_event")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentEvent extends BaseTime {
    @Id
    private String checkoutId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id", nullable = false)
    private Reservation reservation;    //예약정보 확인을 위해

    @Column(nullable = false)
    private String pspToken;

    @Builder.Default
    @Column(nullable = false)
    private boolean isPaymentDone = false;
}
