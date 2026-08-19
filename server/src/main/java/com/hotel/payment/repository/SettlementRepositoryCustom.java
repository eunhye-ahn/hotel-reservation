package com.hotel.payment.repository;

import com.hotel.admin.dto.settlement.SettlementHistoryResponse;
import com.hotel.payment.domain.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SettlementRepositoryCustom {
    Page<SettlementHistoryResponse> searchSettlementByHotel(String sellerAccount,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate,
                                                                   SettlementStatus status,

                                                                   Pageable pageable);
}
