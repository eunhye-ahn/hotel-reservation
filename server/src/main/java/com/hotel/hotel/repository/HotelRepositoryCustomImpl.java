//package com.hotel.hotel.repository;
//
//import com.hotel.hotel_server.domain.Hotel;
//import com.hotel.hotel_server.domain.QHotel;
//import com.hotel.hotel_server.domain.QRoomType;
//import com.hotel.hotel_server.domain.QRoomTypeInventory;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RequiredArgsConstructor
//public class HotelRepositoryCustomImpl implements HotelRepositoryCustom{
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public List<Hotel> findByRegionWithCursor(String lDongRegnCd, String lDongSignguCd,
//                                              LocalDate startDate, LocalDate endDate,
//                                              int numberOfGuests,
//                                              Long cursorId, int size) {
//        QHotel hotel = QHotel.hotel;
//        QRoomType roomType = QRoomType.roomType;
//        QRoomTypeInventory inventory = QRoomTypeInventory.roomTypeInventory;
//
//        return queryFactory
//                .selectFrom(hotel)
//                .join(roomType).on(roomType.hotel.id.eq(hotel.id))
//                .join(inventory).on(
//                        inventory.roomType.eq(roomType)
//                )
//                .where(
//                        regionCondition(hotel, lDongRegnCd),
//                        signguCondition(hotel, lDongSignguCd),
//
//                        cursorCondition(hotel, cursorId)
//                )
//                .orderBy(hotel.id.asc())    //커서 기준과 정렬기준 일치
//                .limit(size+1)              //한개 더 조회 -> hasNext판단용
//                .fetch();                   //쿼리 실행 후 list 반환
//
//    }
//
//    private BooleanExpression regionCondition(QHotel hotel, String lDongRegnCd) {
//        return (lDongRegnCd != null && !lDongRegnCd.isEmpty())
//                ? hotel.lDongRegnCd.eq(lDongRegnCd)
//                : null;
//    }
//
//    private BooleanExpression signguCondition(QHotel hotel, String lDongSignguCd) {
//        return (lDongSignguCd != null && !lDongSignguCd.isEmpty())
//                ? hotel.lDongSignguCd.eq(lDongSignguCd)
//                : null;
//    }
//
//    private BooleanExpression cursorCondition(QHotel hotel, Long cursorId){
//        return cursorId != null? hotel.id.gt(cursorId):null;
//    }
//
//}
