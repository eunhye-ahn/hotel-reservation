package com.hotel.hotel.controller;

import com.hotel.hotel.dto.*;
import com.hotel.hotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels")
public class HotelController {
    private final HotelService hotelService;

//    @GetMapping
//    public ResponseEntity<Page<HotelResponse>> getHotels(@RequestParam(defaultValue = "0") int page){
//        Page<HotelResponse> result = hotelService.getHotels(page);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(result);
//    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDetailResponse> getHotelDetail(@PathVariable Long hotelId,
                                                              @RequestParam(required = false) LocalDate startDate,
                                                              @RequestParam(required = false) LocalDate endDate,
                                                              @RequestParam(required = false, defaultValue = "1") Integer numberOfRooms,
                                                              @RequestParam(required = false) Integer numberOfGuests){
        HotelDetailResponse result = hotelService.getHotelDetail(hotelId, startDate, endDate, numberOfRooms, numberOfGuests);

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

    /**
     * 지역별 호텔 목록 조회(무한스크롤)
     * GET /api/hotels?lDongRegnCd=11&cursorId=10
     *
     * @param lDongRegnCd   지역 코드 (전체조회에서는 없어야하고 필터에서는 필수인데.)..
     * @return cursorId     마지막으로 조회한 호텔 ID (첫 요청 시 null)
     */
    @GetMapping
    public ResponseEntity<CursorResponse> searchByFilter(@RequestParam(required = false) String q,
                                                         @RequestParam(required = false) String lDongRegnCd,
                                                         @RequestParam(required = false) String lDongSignguCd,
                                                         @RequestParam(required = false) String lclsSystm2,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate,
                                                         @RequestParam(required = false) Integer numberOfGuests,
                                                         @RequestParam(required = false, defaultValue = "1") Integer numberOfRooms,
                                                         @RequestParam(required = false) Long cursorId){

        CursorResponse result = hotelService.searchByFilter(q, lDongRegnCd, lDongSignguCd, lclsSystm2, startDate, endDate,
                numberOfGuests, numberOfRooms,
                cursorId);

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
    public ResponseEntity<?> getSimilarHotel(@RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = true) Long hotelId){
        Page<HotelResponse> result = hotelService.getSimilarHotel(page, hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
