package com.hotel.reservation.service;

import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.domain.Room;
import com.hotel.reservation.domain.RoomType;
import com.hotel.reservation.dto.*;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.HotelRepository;
import com.hotel.reservation.repository.RateRepository;
import com.hotel.reservation.repository.RoomTypeInventoryRepository;
import com.hotel.reservation.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RateRepository rateRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    //룸타입 생성
    @Transactional
    public RoomTypeCreateResponse addRoomType(Long hotelId, RoomTypeCreateRequest request){
        //유효성검사
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        //이름 중복
        if(roomTypeRepository.existsByNameAndHotel(request.getRoomTypeName(), hotel)){
            throw new CustomException(ErrorCode.ROOM_TYPE_ALREADY_EXISTS);
        }

        //db저장
        RoomType roomType = roomTypeRepository.save(RoomType.builder()
                .name(request.getRoomTypeName())
                .maxOccupancy(request.getMaxOccupancy())
                .imageUrl(request.getImageUrl())
                .hotel(hotel)
                .build());

        return RoomTypeCreateResponse.from(roomType);
    }

    //름 삭제 -staff,관리자
    @Transactional
    public void deleteRoomType(Long hotelId, Long roomTypeId){
        //엔티티검사
        RoomType roomType = roomTypeRepository.findByIdAndHotelId(roomTypeId, hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        //room삭제
        roomTypeInventoryRepository.deleteByRoomType(roomType);
        rateRepository.deleteByRoomType(roomType);
        roomTypeRepository.delete(roomType);
    }

    //room수정 -staff,관리자
    @Transactional
    public RoomTypeUpdateResponse updateRoomType(Long hotelId, Long roomTypeId, RoomTypeUpdateRequest request){
        //엔티티검사
        RoomType roomType = roomTypeRepository.findByIdAndHotelId(roomTypeId, hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        //hotel수정
        roomType.update(
                request.getRoomTypeName(),
                request.getMaxOccupancy(),
                request.getImageUrl()
        );

        return RoomTypeUpdateResponse.from(roomType);
    }
}
