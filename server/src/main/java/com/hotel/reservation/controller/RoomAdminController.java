package com.hotel.reservation.controller;

import com.hotel.reservation.dto.*;
import com.hotel.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels/{hotelId}/roomtypes/{roomTypeId}")
public class RoomAdminController {
    private final RoomService roomService;

    //호텔생성 -staff,관리자
    @PostMapping
    public ResponseEntity<RoomCreateResponse> createRoom(@PathVariable Long hotelId,
                                                         @PathVariable Long roomTypeId,
                                                         @RequestBody RoomCreateRequest request){
        RoomCreateResponse result = roomService.addRoom(hotelId, roomTypeId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //호텔삭제 -staff,관리자
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long hotelId, @PathVariable Long roomTypeId, @PathVariable Long roomId){
        roomService.deleteRoom(hotelId, roomTypeId, roomId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //호텔수정 -staff,관리자
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomUpdateResponse> updateHotel(@PathVariable Long hotelId, @PathVariable Long roomTypeId, @PathVariable Long roomId,
                                                              @RequestBody RoomUpdateRequest request){
        RoomUpdateResponse result = roomService.updateRoom(hotelId, roomTypeId, roomId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

}
