package com.hotel.reservation.controller;

import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.ReservationDetailResponse;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    //예약생성
    @PostMapping
    public ResponseEntity<ReservationDetailResponse> createReservation(@AuthenticationPrincipal Long userId, @RequestBody ReservationRequest request){
        ReservationDetailResponse result = reservationService.createReservation(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //내 예약조회
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@AuthenticationPrincipal Long userId){
        List<ReservationResponse> result = reservationService.getMyReservations(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //전체예약조회 -관리자
    @GetMapping("/all")
    public ResponseEntity<List<ReservationResponse>> getReservations(){
        List<ReservationResponse> result = reservationService.getReservations();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
