package com.hotel.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private String reservationId; //멱등키(프론트에서 생성) -UUID
    private Long hotelId;
    private Long roomTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfRoomsToReserve;
}