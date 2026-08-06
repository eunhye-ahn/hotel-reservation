package com.hotel.hotel.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.*;
import com.hotel.hotel.dto.*;
import com.hotel.hotel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    private final RateRepository rateRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    //예약폼 -유저용
    public RoomTypeReservationResponse getRoomTypeForReservation(Long hotelId, Long roomTypeId,
                                                                   LocalDate startDate, LocalDate endDate,
                                                                 int numberOfRooms) {
        //유효성검사
        roomTypeRepository.findByIdAndHotelId(roomTypeId, hotelId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        //잔여객실조회
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository.findByRoomTypeIdAndDateBetween(roomTypeId, startDate, endDate.minusDays(1));

        //가격계산
        List<Rate> rates = rateRepository.findByRoomTypeIdAndDateBetween(roomTypeId, startDate, endDate.minusDays(1));

        long expectedDays = ChronoUnit.DAYS.between(startDate, endDate);
        if(rates.size() != expectedDays) {
            throw new CustomException(ErrorCode.RATE_NOT_FOUND);
        }
        if (inventories.size() != expectedDays) {
            throw new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND);
        }

        int totalDemandRate = rates.stream().mapToInt(Rate::getDemandRate).sum();
        int totalPrice = totalDemandRate * numberOfRooms;

        return RoomTypeReservationResponse.from(inventories, totalDemandRate, totalPrice);
    }}
