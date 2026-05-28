package com.hotel.hotel_server.service;

import com.hotel.hotel_server.domain.Hotel;
import com.hotel.hotel_server.domain.Room;
import com.hotel.hotel_server.domain.RoomType;
import com.hotel.hotel_server.dto.*;
import com.hotel.hotel_server.exception.CustomException;
import com.hotel.hotel_server.exception.ErrorCode;
import com.hotel.hotel_server.repository.HotelRepository;
import com.hotel.hotel_server.repository.RoomRepository;
import com.hotel.hotel_server.repository.RoomTypeInventoryRepository;
import com.hotel.hotel_server.repository.RoomTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;

    //룸 생성
    @Transactional
    public RoomCreateResponse addRoom(Long hotelId, Long roomTypeId, RoomCreateRequest request){
        //유효성검사
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        //이름 중복
        if(roomRepository.existsByNameAndRoomTypeAndHotel(request.getRoomName(), roomType, hotel)){
            throw new CustomException(ErrorCode.HOTEL_ALREADY_EXISTS);
        }

        //db저장
        Room room = roomRepository.save(Room.builder()
                        .name(request.getRoomName())
                        .floor(request.getFloor())
                        .number(request.getNumber())
                        .hotel(hotel)
                        .roomType(roomType)
                    .build());

        return RoomCreateResponse.from(room);
    }

    //름 삭제 -staff,관리자
    @Transactional
    public void deleteRoom(Long hotelId, Long roomTypeId, Long roomId){
        //엔티티검사
        Room room = roomRepository.findByIdAndRoomTypeIdAndHotelId(roomId, roomTypeId, hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        //room삭제
        roomRepository.delete(room);
    }

    //room수정 -staff,관리자
    @Transactional
    public RoomUpdateResponse updateRoom(Long hotelId, Long roomTypeId, Long roomId, RoomUpdateRequest request){
        //엔티티검사
        Room room = roomRepository.findByIdAndRoomTypeIdAndHotelId(roomId, roomTypeId, hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        //hotel수정
        room.update(
                request.getRoomName(),
                request.getFloor(),
                request.getNumber(),
                request.getUsable()
        );

        return RoomUpdateResponse.from(room);
    }

    //호텔>객실타입>객실 상세 조회
    public RoomInfoResponse getRoom(Long hotelId, Long roomTypeId, Long roomId){
        //엔티티 검사
        Room room = roomRepository.findByIdAndRoomTypeIdAndHotelId(roomId, roomTypeId, hotelId)
                .orElseThrow(()-> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        return RoomInfoResponse.from(room);
    }
}
