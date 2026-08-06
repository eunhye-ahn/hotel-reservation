package com.hotel.admin.dto.dashboard;

import com.hotel.payment.domain.PaymentOrderStatus;

public record PaymentStatusStaticResponse (
        PaymentOrderStatus status,
        int count
){ }
