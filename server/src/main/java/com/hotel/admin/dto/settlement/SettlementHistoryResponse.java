package com.hotel.admin.dto.settlement;

import com.hotel.payment.domain.Settlement;
import com.hotel.payment.domain.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SettlementHistoryResponse {
    private Long settlementId;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private Integer amount;
    private SettlementStatus status;
    private LocalDateTime settledAt;
    private LocalDateTime createdAt;

    public static SettlementHistoryResponse from(Settlement settlement){
        return SettlementHistoryResponse.builder()
                .settlementId(settlement.getId())
                .amount(settlement.getAmount())
                .periodStartDate(settlement.getPeriodStartDate())
                .periodEndDate(settlement.getPeriodEndDate())
                .status(settlement.getStatus())
                .settledAt(settlement.getSettledAt())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}
