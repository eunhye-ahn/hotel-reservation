package com.hotel.reservation.controller;

import com.hotel.reservation.dto.*;
import com.hotel.reservation.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels")
public class HotelController {
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelResponse>> getHotels(@RequestParam int page){
        Page<HotelResponse> result = hotelService.getHotels(page);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDetailResponse> getHotelDetail(@PathVariable Long hotelId){
        HotelDetailResponse result = hotelService.getHotelDetail(hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //호텔생성 -staff,관리자
    @PostMapping
    public ResponseEntity<HotelCreateResponse> createHotel(@RequestBody HotelCreateRequest request){
        HotelCreateResponse result = hotelService.addHotel(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //호텔삭제 -staff,관리자
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId){
        hotelService.deleteHotel(hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //호텔수정 -staff,관리자
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelUpdateResponse> updateHotel(@PathVariable Long hotelId,
                                                           @RequestBody HotelUpdateRequest request){
        HotelUpdateResponse result = hotelService.updateHotel(hotelId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
