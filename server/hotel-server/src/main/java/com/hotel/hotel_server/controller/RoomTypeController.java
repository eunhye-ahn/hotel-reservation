package com.hotel.hotel_server.controller;

import com.hotel.hotel_server.dto.RoomTypeReservationResponse;
import com.hotel.hotel_server.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotels/{hotelId}/roomTypes")
public class RoomTypeController {
    private final RoomTypeService roomTypeService;

    //유저 -예약폼
    @GetMapping("/{roomTypeId}/reservation") //유저인증필요
    public ResponseEntity<RoomTypeReservationResponse> getRoomTypeForReservation(@PathVariable Long hotelId,
                                                                                 @PathVariable Long roomTypeId,
                                                                                 @RequestParam LocalDate startDate,
                                                                                 @RequestParam LocalDate endDate,
                                                                                 @RequestParam int numberOfRooms) {
        RoomTypeReservationResponse result = roomTypeService.getRoomTypeForReservation(hotelId, roomTypeId, startDate, endDate, numberOfRooms);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}