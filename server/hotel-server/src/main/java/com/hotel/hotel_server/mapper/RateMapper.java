package com.hotel.hotel_server.mapper;

import com.hotel.hotel_server.domain.Hotel;
import com.hotel.hotel_server.dto.CheapestRateResult;
import com.hotel.hotel_server.dto.HotelResponse;
import com.hotel.hotel_server.dto.HotelSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * //날짜 필터링 조회 -기간 내 가장 저렴한 객실의 가격 반환
 *     private HotelResponse toResponse(Hotel hotel, LocalDate startDate, LocalDate endDate){
 *         Rate cheapestRate = rateRepository.findCheapestRate(hotel.getId(), startDate)
 *                 //복잡한 쿼리 - mybatis로 처리
 *     }
 */
@Mapper
public interface RateMapper {
    //기간 내 객실 타입별 요금 합산 후 가장 저렴한 금액 반환
    CheapestRateResult findCheapestTotalRate(
        @Param("hotelId") Long hotelId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
