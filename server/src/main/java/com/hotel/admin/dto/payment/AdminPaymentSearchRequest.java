package com.hotel.admin.dto.payment;

import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.domain.PaymentSearchType;

import java.time.LocalDate;

public record AdminPaymentSearchRequest (
        PaymentSearchType searchType,
        String keyword,
        LocalDate startDate,
        LocalDate endDate,
        PaymentOrderStatus status,
        int page
){}
