package com.hotel.admin.controller;

import com.hotel.admin.dto.reservation.*;
import com.hotel.admin.service.AdminReservationService;
import com.hotel.reservation.domain.ReservationSearchType;
import com.hotel.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/reservation")
public class AdminReservationController {
    private final AdminReservationService adminReservationService;

    //예약 조회 (필터)
    @GetMapping
    public ResponseEntity<Page<AdminReservationSearchResponse>> getReservations(@RequestParam(required = false)LocalDate startDate,
                                                                                @RequestParam(required = false)LocalDate endDate,
                                                                                @RequestParam(required = false)ReservationSearchType searchType,
                                                                                @RequestParam(required = false)String keyword,
                                                                                @RequestParam(required = false)ReservationStatus status,
                                                                                @RequestParam(required = false) Boolean roomAssigned,
                                                                                @RequestParam(required = false, defaultValue = "0")int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<AdminReservationSearchResponse> result = adminReservationService.getReservations(startDate, endDate, searchType, keyword, status, roomAssigned, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //예약 상세
    @GetMapping("/{reservationId}")
    public ResponseEntity<AdminReservationDetailResponse> getReservationDetail(@PathVariable Long reservationId){

        AdminReservationDetailResponse result = adminReservationService.getReservationDetail(reservationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //방 조회
    @GetMapping("/{reservationId}/rooms")
    public ResponseEntity<List<AdminRoomResponse>> getRoomsByReservation(@PathVariable Long reservationId){
        List<AdminRoomResponse> list = adminReservationService.getRoomsByReservation(reservationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    //배정확정
    @PatchMapping("/{reservationId}/assign-rooms")
    public ResponseEntity<Void> assignRoom(
            @PathVariable Long reservationId,
            @RequestBody AssignmentRoomRequest request){
        adminReservationService.assignRoom(reservationId, request.getRoomId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //배정취소
    @PatchMapping("/{reservationId}/unassign-rooms")
    public ResponseEntity<Void> unassignRoom(@PathVariable Long reservationId){
        adminReservationService.unassignRoom(reservationId);

        return  ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //예약취소-관리자
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservationByAdmin(@PathVariable Long reservationId,
                                                  @RequestBody CancelReservationByAdminRequest request){

        adminReservationService.refundByReservation(reservationId, request.getCancelReason());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
