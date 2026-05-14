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
    private String reservationKey;
    private String hotelName;
    private String roomTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String hotelImageUrl;
    private ReservationStatus reservationStatus;

    public static ReservationResponse from(Reservation reservation){
        return ReservationResponse.builder()
                .reservationKey(reservation.getReservationKey())
                .hotelName(reservation.getHotel().getName())
                .roomTypeName(reservation.getRoomType().getName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .checkInTime(reservation.getHotel().getCheckInTime())
                .checkOutTime(reservation.getHotel().getCheckOutTime())
                .hotelImageUrl(reservation.getHotel().getImageUrl()) //n+1
                .reservationStatus(reservation.getReservationStatus())
                .build();
    }
}
