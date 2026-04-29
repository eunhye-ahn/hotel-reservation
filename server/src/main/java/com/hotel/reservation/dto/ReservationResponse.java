package com.hotel.reservation.dto;

import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationResponse {
    private Long reservationId;
    private String hotelName;
    private String roomTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private ReservationStatus reservationStatus;

    public static ReservationResponse from(Reservation reservation){
        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .hotelName(reservation.getHotel().getName())
                .roomTypeName(reservation.getRoomType().getName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .checkInTime(reservation.getHotel().getCheckInTime())
                .checkOutTime(reservation.getHotel().getCheckOutTime())
                .reservationStatus(reservation.getReservationStatus())
                .build();
    }

}
