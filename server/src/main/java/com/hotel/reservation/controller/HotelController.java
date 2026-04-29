package com.hotel.reservation.controller;

import com.hotel.reservation.dto.HotelDetailResponse;
import com.hotel.reservation.dto.HotelResponse;
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
}
