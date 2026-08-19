package com.hotel.hotel.repository;

import com.hotel.admin.dto.inventory.AdminRoomInfoResponse;
import com.hotel.reservation.domain.ReservationStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.hotel.hotel.domain.QRoom.room;
import static com.hotel.reservation.domain.QReservation.reservation;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminRoomInfoResponse> searchByRoomInfo(Long hotelId, Long roomTypeId, Integer floor, LocalDate targetDate , Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(room.roomType.hotel.id.eq(hotelId));

        if(roomTypeId != null){
            builder.and(room.roomType.id.eq(roomTypeId));
        }
        if(floor != null){
            builder.and(room.floor.eq(floor));
        }

        List<AdminRoomInfoResponse> content = queryFactory
                .select(Projections.constructor(AdminRoomInfoResponse.class,
                        room.id,
                        room.name,
                        room.floor,
                        room.number,
                        room.roomType.name,
                        room.usable,
                        //예약생성일/룸id의 해당하는 룸 반환
                        //true / false 여부인데... => 8.18
                        //start 8.15 ~ end 8. 19 =>
                        new CaseBuilder()
                                .when(JPAExpressions.selectOne().from(reservation)
                                        .where(
                                                reservation.room.id.eq(room.id),
                                                reservation.startDate.loe(targetDate),
                                                reservation.endDate.gt(targetDate),
                                                reservation.reservationStatus.eq(ReservationStatus.BEFORE_USE)
                                        )
                                        .exists())
                                .then(false)
                                .otherwise(true)
                ))
                .from(room)
                .where(builder)
                .orderBy(room.floor.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(room.count())
                .from(room)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
