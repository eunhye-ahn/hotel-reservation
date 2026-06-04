package com.hotel.hotel_server.service;

import com.hotel.hotel_server.domain.Hotel;
import com.hotel.hotel_server.domain.Rate;
import com.hotel.hotel_server.domain.RoomTypeInventory;
import com.hotel.hotel_server.dto.*;
import com.hotel.hotel_server.exception.CustomException;
import com.hotel.hotel_server.exception.ErrorCode;
import com.hotel.hotel_server.mapper.HotelMapper;
import com.hotel.hotel_server.mapper.RateMapper;
import com.hotel.hotel_server.repository.HotelRepository;
import com.hotel.hotel_server.repository.RateRepository;
import com.hotel.hotel_server.repository.RoomTypeInventoryRepository;
import com.hotel.hotel_server.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final int PAGE_SIZE = 21;
    private final HotelRepository hotelRepository;
    private final RateRepository rateRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    private final RateMapper rateMapper;
    private final HotelMapper hotelMapper;

    LocalDate today = LocalDate.now();

    //호텔 내 객실 조회
    public HotelDetailResponse getHotelDetail(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        List<RoomTypeResponse> roomTypes = roomTypeRepository.findByHotelId(hotelId)
                .stream().map(rt -> {
                    Rate rate = rateRepository.findByRoomTypeAndDate(rt, today)
                            .orElseThrow(()->new CustomException(ErrorCode.RATE_NOT_FOUND));
                    RoomTypeInventory inventory = roomTypeInventoryRepository.findByRoomTypeAndDate(rt, today)
                            .orElseThrow(()->new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND));
                    return RoomTypeResponse.from(rt, rate, inventory);
                }).toList();

        return HotelDetailResponse.builder()
                .hotelId(hotelId)
                .hotelName(hotel.getName())
                .address(hotel.getAddress())
                .imageUrl(hotel.getImageUrl())
                .checkInTime(hotel.getCheckInTime())
                .checkOutTime(hotel.getCheckOutTime())
                .roomTypes(roomTypes)
                .build();
    }


    //호텔 생성
    @Transactional
    public HotelCreateResponse addHotel(HotelCreateRequest request){
        //유효성검사
        //이름이나 주소중복
        if(hotelRepository.existsByName(request.getHotelName()) || hotelRepository.existsByAddress(request.getAddress())){
            throw new CustomException(ErrorCode.HOTEL_ALREADY_EXISTS);
        }

        //db저장
        Hotel hotel = hotelRepository.save(Hotel.builder()
                        .name(request.getHotelName())
                        .address(request.getAddress())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .imageUrl(request.getImageUrl())
                        .checkInTime(request.getCheckInTime())
                        .checkOutTime(request.getCheckOutTime())
                .build());

        return HotelCreateResponse.from(hotel);
    }

    //호텔삭제 -staff,관리자
    public void deleteHotel(Long hotelId){
        //엔티티검사
        Hotel hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(()-> new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        //hotel삭제
        hotelRepository.delete(hotel);
    }

    //호텔수정 -staff,관리자
    public HotelUpdateResponse updateHotel(Long hotelId, HotelUpdateRequest request){
        //엔티티검사
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        //hotel수정
        hotel.update(
                request.getHotelName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getImageUrl(),
                request.getCheckInTime(),
                request.getCheckOutTime()
        );

        return HotelUpdateResponse.from(hotel);
    }

    //조회(전체조회 / 필터조회)
    public CursorResponse searchByFilter(String lDongRegnCd,String lDongSignguCd,
                                         LocalDate startDate, LocalDate endDate,
                                         Integer numberOfGuests, //게스트 수 처리..........
                                         Integer numberOfRooms,
                                         Long cursorId){

        //available
        //totalDays

        //List<Hotel> hotels = hotelRepository.findByRegionWithCursor(lDongRegnCd, lDongSignguCd, int numberOfGuests, cursorId, PAGE_SIZE);
        //여기서 mybatis로 동적쿼리 필터링으로 변경할 것
        HotelSearchParam param = HotelSearchParam.builder()
                .lDongRegnCd(lDongRegnCd)
                .lDongSignguCd(lDongSignguCd)
                .startDate(startDate)
                .endDate(endDate)
                .numberOfGuests(numberOfGuests)
                .numberOfRooms(numberOfRooms)
                .cursorId(cursorId)
                .today(LocalDate.now())
                .size(PAGE_SIZE+1)
                .build();

        List<Hotel> hotels = hotelMapper.findByHotelFilter(param);

        //요금계산
        List<HotelResponse> list = hotels.stream()
                .map(hotel -> startDate != null && endDate != null
                    ? toResponse(hotel, startDate, endDate) //날짜 있으면 기간 계산
                    : toResponse(hotel))                    //없으면 오늘 기준(기간내합산로직X)
                .toList();

        return CursorResponse.of(list, PAGE_SIZE);
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
}
