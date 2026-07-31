package com.hotel.admin.dto.dashboard;

import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.reservation.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatusStaticResponse {
    private PaymentOrderStatus status;
    private int count;
}
