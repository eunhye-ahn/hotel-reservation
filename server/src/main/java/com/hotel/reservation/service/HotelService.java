package com.hotel.reservation.service;

import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.domain.Rate;
import com.hotel.reservation.domain.RoomType;
import com.hotel.reservation.domain.RoomTypeInventory;
import com.hotel.reservation.dto.*;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.HotelRepository;
import com.hotel.reservation.repository.RateRepository;
import com.hotel.reservation.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    LocalDate today = LocalDate.now();

    //호텔조회
    public Page<HotelResponse> getHotels(int page){

        Pageable pageable = PageRequest.of(page,PAGE_SIZE);
        return hotelRepository.findAllWithRate(today, pageable)
                .map(hotel -> {
                    Rate cheapestRate = rateRepository.findCheapestRate(hotel.getId(), today)
                            .orElseThrow(()->new CustomException(ErrorCode.RATE_NOT_FOUND));
                    return HotelResponse.from(hotel, cheapestRate);
                });
    }

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
}
