package com.hotel.payment.repository;

import com.hotel.admin.dto.settlement.SettlementHistoryResponse;
import com.hotel.payment.domain.SettlementSortType;
import com.hotel.payment.domain.SettlementStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static com.hotel.payment.domain.QSettlement.settlement;

@RequiredArgsConstructor
public class SettlementRepositoryImpl implements SettlementRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SettlementHistoryResponse> searchSettlementByHotel(String sellerAccount, LocalDate startDate, LocalDate endDate, SettlementStatus status, Pageable pageable) {
        //쿼리만들기
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(settlement.sellerAccount.eq(sellerAccount));

        if (startDate != null) {
            builder.and(settlement.createdAt.goe(startDate.atTime(0,0,0)));
        }

        if (endDate != null) {
            builder.and(settlement.createdAt.loe(endDate.atTime(23,59,59)));
        }

        if(status != null){
            builder.and(settlement.status.eq(status));
        }

        List<SettlementHistoryResponse> content = queryFactory.select(Projections.constructor(SettlementHistoryResponse.class,
                        settlement.id,
                        settlement.periodStartDate,
                        settlement.periodEndDate,
                        settlement.amount,
                        settlement.status,
                        settlement.createdAt,
                        settlement.settledAt
                ))
                        .from(settlement)
                        .where(builder)
                        .orderBy(settlement.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(settlement.count())
                .from(settlement)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total!=null ? total : 0L);
    }
}
