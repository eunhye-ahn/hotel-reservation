package com.hotel.reservation.controller;

import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.dto.*;
import com.hotel.reservation.service.ReservationService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ReservationCreateResponse> createReservation(@AuthenticationPrincipal Long userId,
                                                                       @RequestBody @Valid ReservationRequest request){
        ReservationCreateResponse result = reservationService.createReservation(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //내 예약조회
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@AuthenticationPrincipal Long userId,
                                                                       @RequestParam ReservationStatus status){
        List<ReservationResponse> result = reservationService.getMyReservations(userId, status);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //예약상세조회 -예약확인서
    @GetMapping("/{reservationKey}")
    public ResponseEntity<ReservationDetailResponse> reservationConfirm(@AuthenticationPrincipal Long userId,
                                                                        @PathVariable String reservationKey){
        ReservationDetailResponse result = reservationService.reservationConfirm(userId, reservationKey);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //결제창
    @GetMapping("/{reservationKey}/reservation-info")
    public ResponseEntity<ReservationInfoResponse> reservationInfo(@AuthenticationPrincipal Long userId,
                                                                   @PathVariable String reservationKey){
        ReservationInfoResponse result = reservationService.getReservationInfo(userId, reservationKey);

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

    //예약 취소
    @DeleteMapping("{reservationKey}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal Long userId, @PathVariable String reservationKey){
        reservationService.deleteReservation(userId, reservationKey);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * 결제서버에서 예약 유효성 확인할 때 호출
     */
    @GetMapping("/{reservationKey}/payment-info")
    public ResponseEntity<ReservationFeignResponse> getReservationForPayment(@PathVariable String reservationKey){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationService.getReservationForPayment(reservationKey)
        );
    }

    //폴링 결제 후 예약상태 확정 확인을 위해
    @GetMapping("/{reservationKey}/status")
    public ResponseEntity<String> getReservationStatus(@PathVariable String reservationKey){
        String result = reservationService.getReservationStatus(reservationKey);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
