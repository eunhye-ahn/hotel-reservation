package com.hotel.admin.service;

import com.hotel.admin.dto.payment.AdminPaymentResponse;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.domain.PaymentSearchType;
import com.hotel.payment.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {
    private final PaymentOrderRepository paymentOrderRepository;

    public Page<AdminPaymentResponse> getPayments(PaymentSearchType searchType, String keyword, LocalDate startDate, LocalDate endDate, PaymentOrderStatus status, Pageable pageable) {
        return  paymentOrderRepository
                .searchByPayment(startDate, endDate, searchType, keyword, status, pageable);
    }
}
