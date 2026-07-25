package com.hotel.admin.controller;

import com.hotel.admin.dto.AdminReservationDetailResponse;
import com.hotel.admin.dto.AdminReservationSearchResponse;
import com.hotel.admin.dto.AdminRoomListResponse;
import com.hotel.admin.dto.AssignmentRoomRequest;
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

/**
 * GET /admin/reservations (필터+페이징)
 * GET /admin/reservations/{id}
 * PATCH /admin/reservations/{id}/assign-room
 * PATCH /admin/reservations/{id}/status
 * POST /admin/reservations/{id}/cancel (환불트리거 포함)
 */

/**
 * -필터(날짜범위, 호텔선택, 상태(예약상태)) 드롭다운
 *
 * 예약번호,예약자,호텔명,객실타입,체크인/아웃일,상태뱃지,배정여부
 */

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
    public ResponseEntity<List<AdminRoomListResponse>> getRoomsByReservation(@PathVariable Long reservationId){
        List<AdminRoomListResponse> list = adminReservationService.getRoomsByReservation(reservationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    //배정확정
    @PostMapping("/{reservationId}/assign-rooms")
    public ResponseEntity<Void> assignRoom(
            @PathVariable Long reservationId,
            @RequestBody AssignmentRoomRequest request){
        adminReservationService.assignRoom(reservationId, request.getRoomId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
