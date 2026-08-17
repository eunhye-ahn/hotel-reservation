package com.hotel.hotel.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.Rate;
import com.hotel.hotel.dto.*;
import com.hotel.hotel.mapper.HotelMapper;
import com.hotel.hotel.mapper.RateMapper;
import com.hotel.hotel.mapper.RoomTypeMapper;
import com.hotel.hotel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {
    private final HotelRepository hotelRepository;
    private final RateRepository rateRepository;
    private final RateMapper rateMapper;
    private final HotelMapper hotelMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final HotelSearchQueryRepository hotelSearchQueryRepository;

    LocalDate today = LocalDate.now();

    //호텔 내 객실 조회
    public HotelDetailResponse getHotelDetail(Long hotelId, LocalDate startDate,LocalDate endDate, Integer numberOfRooms, Integer numberOfGuests) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        //totalDays 계산
        int totalDays = (startDate != null && endDate != null)
                ?((int) ChronoUnit.DAYS.between(startDate, endDate)) : 0;

        RoomTypeInventoryParam param = new RoomTypeInventoryParam(
                hotelId,
                today,
                startDate,
                endDate != null ? endDate.minusDays(1) : null,
                totalDays,
                numberOfRooms,
                numberOfGuests
        );

        List<RoomTypeResponse> roomTypes = roomTypeMapper.findByRoomTypeFilter(param);

        return HotelDetailResponse.from(hotel,roomTypes);
    }

    //홈 -인기순정렬
    public List<HotelResponse> getPopularHotels(Pageable pageable){
        List<Hotel> hotels = hotelRepository.findPopularByWishCount(pageable);

        return hotels.stream()
                .map(this::toResponse)
                .toList();
    }

    //조회(전체조회 / 필터조회)
    public Page<HotelResponse> searchByFilter(String q,
                                         String lDongRegnCd,String lDongSignguCd,
                                         String lclsSystm2,
                                         LocalDate startDate, LocalDate endDate,
                                         Integer numberOfGuests, //게스트 수 처리..........
                                         Integer numberOfRooms,
                                         Pageable pageable){

        //검색어 있는 경우
        List<Long> hotelIds = null;
        if(StringUtils.hasText(q)){
            hotelIds = hotelSearchQueryRepository.search(q);
        }
        log.info("es result hotelIds: {}", hotelIds);

        //totalDays 계산
        int totalDays = (startDate != null && endDate != null)
        ?((int) ChronoUnit.DAYS.between(startDate, endDate)) : 0;

        HotelSearchParam param = new HotelSearchParam(
                hotelIds,
                lDongRegnCd,
                lDongSignguCd,
                lclsSystm2,
                startDate,
                endDate,
                numberOfGuests,
                numberOfRooms,
                LocalDate.now(),
                totalDays,
                pageable.getOffset(),
                pageable.getPageSize()
        );

        List<Hotel> hotels = hotelMapper.findByHotelFilter(param);
        long totalCount = hotelMapper.countByHotelFilter(param);

        //요금계산
        List<HotelResponse> list = hotels.stream()
                .map(hotel -> startDate != null && endDate != null
                    ? toResponse(hotel, startDate, endDate) //날짜 있으면 기간 계산
                    : toResponse(hotel))                    //없으면 오늘 기준(기간내합산로직X)
                .toList();



        return new PageImpl<>(list, pageable, totalCount);
    }

    //전체조회 -오늘날짜 기준 가장 저렴한 객실의 가격 반환
    private HotelResponse toResponse(Hotel hotel){
        Rate cheapestRate = rateRepository.findCheapestRate(hotel.getId(), LocalDate.now())
                .orElse(null);
        return HotelResponse.from(hotel, cheapestRate);
    }
    //기간 내 객실 타입별 요금 합산 후 가장 저렴한 금액 반환
    private HotelResponse toResponse(Hotel hotel, LocalDate startDate, LocalDate endDate){
        CheapestRateResult rate = rateMapper.findCheapestTotalRate(
                hotel.getId(),
                startDate,
                endDate
        );
        return HotelResponse.from(hotel, rate);
    }



    //ES
    //자동완성
    public List<String> autocomplete(String q){
        return hotelSearchQueryRepository.autocomplete(q);
    }

    //최근 본 호텔과 비슷한 호텔 조회
    public List<HotelResponse> getSimilarHotel(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        //조건에 맞는 호텔 15개 반환
        List<Hotel> hotels = hotelRepository.findSimilarTop15(hotel.getLclsSystm2(), hotel.getLDongRegnCd(), hotelId, PageRequest.of(0,15));

        return hotels.stream().map(this::toResponse).toList();
    }
}
