package com.hotel.hotel.controller;

import com.hotel.hotel.dto.*;
import com.hotel.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels")
public class HotelController {
    private final HotelService hotelService;
    //호텔상세
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDetailResponse> getHotelDetail(@PathVariable Long hotelId,
                                                              @ModelAttribute RoomTypeSearchParam request){
        HotelDetailResponse result = hotelService.getHotelDetail(hotelId,
                request.startDate(),
                request.endDate(),
                request.numberOfRooms(),
                request.numberOfGuests()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //홈 - 페이지네이션 (인기)
    @GetMapping
    public ResponseEntity<List<HotelResponse>> getPopularHotel(){
        Pageable pageable = PageRequest.of(0, 20);
        List<HotelResponse> result = hotelService.getPopularHotels(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<HotelResponse>> searchByFilter(@ModelAttribute HotelSearchRequest request){

        Pageable pageable = PageRequest.of(request.page(), 20);
        Page<HotelResponse> result = hotelService.searchByFilter(
                request.q(),
                request.lDongRegnCd(),
                request.lDongSignguCd(),
                request.lclsSystm2(),
                request.startDate(),
                request.endDate(),
                request.numberOfGuests(),
                request.numberOfRooms(),
                pageable
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getSearchAutocomplete(@RequestParam(required = false) String q){
        List<String> result = hotelService.autocomplete(q);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //최근본 호텔 기준
    @GetMapping("/similarHotel")
    public ResponseEntity<List<HotelResponse>> getSimilarHotel(@RequestParam(required = true) Long hotelId){
        List<HotelResponse> result = hotelService.getSimilarHotel(hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
