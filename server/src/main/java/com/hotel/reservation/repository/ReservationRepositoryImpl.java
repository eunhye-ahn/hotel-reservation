package com.hotel.reservation.repository;

import com.hotel.reservation.domain.ReservationSearchType;
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
import static com.hotel.user.domain.QUser.user;
import static com.hotel.hotel.domain.QHotel.hotel;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> searchByReservation(LocalDate startDate, LocalDate endDate, ReservationSearchType searchType, String keyword, ReservationStatus status, Boolean roomAssigned, Pageable pageable) {
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
                case PHONE -> builder.and(reservation.user.phone.containsIgnoreCase(keyword));
                case RESERVE_ID -> builder.and(reservation.displayReservationNO.containsIgnoreCase(keyword));
            }
        }

        //상태 필터
        if(status != null){
            builder.and(reservation.reservationStatus.eq(status));
        }

        //룸배정여부 필터
        if(roomAssigned != null){
            if(roomAssigned){
                builder.and(reservation.room.isNotNull());
            }else{
                builder.and(reservation.room.isNull());
            }
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

    @Override
    public boolean existsOverlappingReservation(Long roomId, LocalDate start, LocalDate end, Long excludeReservationId) {
        Integer result = queryFactory
                .selectOne()
                .from(reservation)
                .where(
                        reservation.room.id.eq(roomId),
                        reservation.id.ne(excludeReservationId),
                        reservation.reservationStatus.in(ReservationStatus.BEFORE_USE, ReservationStatus.AFTER_USE),
                        reservation.startDate.lt(end),
                        reservation.endDate.gt(start)
                )
                .fetchFirst();

        return result!=null;
    }

}
