package com.hotel.payment.domain;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="settlement")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,  unique = true, name="settlement_key")
    private String settlementKey;

    @Column(nullable = false)
    private String sellerAccount;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDate periodStartDate;

    @Column(nullable = false)
    private LocalDate periodEndDate;

    //실제 정산완료 처리 시각 => pending일때는 null
    private LocalDateTime settledAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private SettlementStatus status = SettlementStatus.PENDING;

    public void complete(){
        if(this.status != SettlementStatus.PENDING){
            throw new CustomException(ErrorCode.INVALID_SETTLEMENT_STATUS);
        }
        this.status = SettlementStatus.COMPLETED;
        this.settledAt = LocalDateTime.now();
    }

    public void fail(){
        this.status = SettlementStatus.FAILED;
    }

    public void needsReconciliation() {this.status = SettlementStatus.NEEDS_RECONCILIATION;}
}
