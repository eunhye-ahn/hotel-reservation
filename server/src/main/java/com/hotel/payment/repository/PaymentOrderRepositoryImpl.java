package com.hotel.payment.repository;

import com.hotel.admin.dto.payment.AdminPaymentResponse;
import com.hotel.payment.domain.PaymentOrderStatus;
import com.hotel.payment.domain.PaymentSearchType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import static com.hotel.payment.domain.QPaymentEvent.paymentEvent;
import static com.hotel.payment.domain.QPaymentOrder.paymentOrder;
import static com.hotel.reservation.domain.QReservation.reservation;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class PaymentOrderRepositoryImpl implements PaymentOrderRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminPaymentResponse> searchByPayment(LocalDate startDate, LocalDate endDate,
                                                      PaymentSearchType searchType, String keyword,
                                                      PaymentOrderStatus status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(keyword) && searchType != null){
            switch (searchType) {
                case HOTEL_NAME -> builder.and(reservation.hotel.name.containsIgnoreCase(keyword));
                case USER_NAME -> builder.and(reservation.user.name.containsIgnoreCase(keyword));
            }
        }

        if (startDate != null) {
            builder.and(paymentOrder.createdAt.goe(startDate.atTime(0,0,0)));
        }

        if (endDate != null) {
            builder.and(paymentOrder.createdAt.loe(endDate.atTime(23,59,59)));
        }

        if(status != null){
            builder.and(paymentOrder.paymentOrderStatus.eq(status));
        }

        List<AdminPaymentResponse> payments = queryFactory
                .select(Projections.constructor(AdminPaymentResponse.class,
                        paymentOrder.paymentOrderId,
                        reservation.hotel.name,
                        reservation.user.name,
                        paymentOrder.amount,
                        paymentOrder.paymentOrderStatus,
                        paymentOrder.createdAt
                ))
                .from(paymentOrder)
                .join(paymentEvent).on(paymentEvent.checkoutId.eq(paymentOrder.checkoutId))
                .join(reservation).on(reservation.id.eq(paymentEvent.reservationId))
                .where(builder)
                .orderBy(paymentOrder.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(paymentOrder.count())
                .from(paymentOrder)
                .join(paymentEvent).on(paymentEvent.checkoutId.eq(paymentOrder.checkoutId))
                .join(reservation).on(reservation.id.eq(paymentEvent.reservationId))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(payments,pageable, total != null ? total : 0L);
    }
}
