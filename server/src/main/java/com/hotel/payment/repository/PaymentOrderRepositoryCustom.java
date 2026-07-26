package com.hotel.payment.repository;

import com.hotel.admin.dto.payment.AdminPaymentResponse;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.domain.PaymentSearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PaymentOrderRepositoryCustom {
    Page<AdminPaymentResponse> searchByPayment(
            LocalDate startDate,
            LocalDate endDate,
            PaymentSearchType searchType,
            String keyword,
            PaymentOrderStatus status,
            Pageable pageable);
}
