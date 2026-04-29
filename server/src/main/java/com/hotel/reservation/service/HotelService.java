package com.hotel.reservation.service;

import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.domain.Rate;
import com.hotel.reservation.domain.RoomType;
import com.hotel.reservation.domain.RoomTypeInventory;
import com.hotel.reservation.dto.HotelDetailResponse;
import com.hotel.reservation.dto.HotelResponse;
import com.hotel.reservation.dto.RoomTypeResponse;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.HotelRepository;
import com.hotel.reservation.repository.RateRepository;
import com.hotel.reservation.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.repository.RoomTypeRepository;
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

    //호텔조회
    public Page<HotelResponse> getHotels(int page){
        LocalDate today = LocalDate.now();
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

        LocalDate today = LocalDate.now();

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
}
