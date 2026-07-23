package com.hotel.admin.controller;

import com.hotel.admin.dto.AdminReservationSearchResponse;
import com.hotel.admin.service.AdminReservationService;
import com.hotel.hotel.domain.ReservationSearchType;
import com.hotel.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

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

    @GetMapping
    public ResponseEntity<Page<AdminReservationSearchResponse>> getReservations(@RequestParam(required = false)LocalDate startDate,
                                                                                @RequestParam(required = false)LocalDate endDate,
                                                                                @RequestParam(required = false)ReservationSearchType searchType,
                                                                                @RequestParam(required = false)String keyword,
                                                                                @RequestParam(required = false)ReservationStatus status,
                                                                                @RequestParam(required = false, defaultValue = "0")int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<AdminReservationSearchResponse> result = adminReservationService.getReservations(startDate, endDate, searchType, keyword, status, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
