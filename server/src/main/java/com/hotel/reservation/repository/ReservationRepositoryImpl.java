package com.hotel.reservation.repository;

import com.hotel.admin.dto.AdminReservationSearchRequest;
import com.hotel.hotel.domain.ReservationSearchType;
import com.hotel.reservation.domain.QReservation;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

//static 선언
import static com.hotel.reservation.domain.QReservation.reservation;
import static com.hotel.reservation.domain.QUser.user;
import static com.hotel.hotel.domain.QHotel.hotel;
import static com.hotel.hotel.domain.QRoomType.roomType;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> searchByReservation(LocalDate startDate, LocalDate endDate, ReservationSearchType searchType, String keyword, ReservationStatus status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        //날짜 필터
        if(startDate != null){
            builder.and(reservation.startDate.goe(startDate));
        }
        if(endDate != null){
            builder.and(reservation.endDate.loe(endDate));
        }

        //드롭다운으로 고른 대상에 따라 분기
        if(StringUtils.hasText(keyword)){
            switch (searchType){
                case USER_NAME -> builder.and(reservation.user.name.containsIgnoreCase(keyword));
                case HOTEL_NAME -> builder.and(reservation.hotel.name.containsIgnoreCase(keyword));
                case RESERVATION_KEY -> builder.and(reservation.reservationKey.eq(keyword));
            }
        }

        //상태 필터
        if(status != null){
            builder.and(reservation.reservationStatus.eq(status));
        }

        List<Reservation> content = queryFactory
                .selectFrom(reservation)
                .join(reservation.user,user).fetchJoin()
                .join(reservation.hotel,hotel).fetchJoin()
                .join(reservation.roomType).fetchJoin()
                .where(builder)
                .orderBy(reservation.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content,pageable,total != null ? total:0L);
    }

}
