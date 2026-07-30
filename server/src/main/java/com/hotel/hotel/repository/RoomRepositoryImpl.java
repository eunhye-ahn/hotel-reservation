package com.hotel.hotel.repository;

import com.hotel.admin.dto.inventory.AdminRoomInfoResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static com.hotel.hotel.domain.QRoom.room;

import java.util.List;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminRoomInfoResponse> searchByRoomInfo(Long hotelId, Long roomTypeId, Integer floor, Pageable pageable) {
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
                        room.usable
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
