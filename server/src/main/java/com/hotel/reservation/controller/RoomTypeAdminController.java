package com.hotel.reservation.controller;

import com.hotel.reservation.dto.*;
import com.hotel.reservation.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 1. 호텔 목록 조회
 *    GET /api/admin/hotels
 *
 * 2. 호텔 클릭 → 호텔 상세 + 룸타입 목록
 *    GET /api/admin/hotels/{hotelId}
 *
 * 3. 룸타입 클릭 → 룸 목록
 *    GET /api/admin/hotels/{hotelId}/room-types/{roomTypeId}/rooms
 *
 * 4. 룸 생성
 *    POST /api/admin/hotels/{hotelId}/room-types/{roomTypeId}/rooms
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels/{hotelId}/roomtypes")
public class RoomTypeAdminController {

    private final RoomTypeService roomTypeService;

    //호텔생성 -staff,관리자
    @PostMapping
    public ResponseEntity<RoomTypeCreateResponse> createRoomType(@PathVariable Long hotelId, @RequestBody RoomTypeCreateRequest request){
        RoomTypeCreateResponse result = roomTypeService.addRoomType(hotelId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //호텔삭제 -staff,관리자
    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long hotelId,
                                               @PathVariable Long roomTypeId){
        roomTypeService.deleteRoomType(hotelId, roomTypeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //호텔수정 -staff,관리자
    @PutMapping("/{roomTypeId}")
    public ResponseEntity<RoomTypeUpdateResponse> updateHotel(@PathVariable Long hotelId,
                                                              @PathVariable Long roomTypeId,
                                                          @RequestBody RoomTypeUpdateRequest request){
        RoomTypeUpdateResponse result = roomTypeService.updateRoomType(hotelId, roomTypeId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{roomTypeId}")
    public ResponseEntity<RoomTypeDetailResponse> getRoomTypeDetail(@PathVariable Long hotelId,
                                                                    @PathVariable Long roomTypeId){
        RoomTypeDetailResponse result = roomTypeService.getRoomTypeDetail(hotelId, roomTypeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
