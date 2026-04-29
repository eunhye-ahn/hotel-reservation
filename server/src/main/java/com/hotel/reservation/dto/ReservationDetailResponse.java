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
public class ReservationDetailResponse {
    private Long reservationId;
    private String hotelName;
    private String roomTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private int numberOfRooms; //예약객실수
    private int totalPrice; //결제금액
    private PaymentStatus status;
    private LocalDateTime createdAt;

    //reservation -> dto변환
    public static ReservationDetailResponse from(Reservation reservation){
        return ReservationDetailResponse.builder()
                .reservationId(reservation.getId())
                .hotelName(reservation.getHotel().getName())
                .roomTypeName(reservation.getRoomType().getName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .checkInTime(reservation.getHotel().getCheckInTime())
                .checkOutTime(reservation.getHotel().getCheckOutTime())
                .numberOfRooms(reservation.getNumberOfRooms())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getPaymentStatus())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
