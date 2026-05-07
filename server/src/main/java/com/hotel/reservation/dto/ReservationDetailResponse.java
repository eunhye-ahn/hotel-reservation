package com.hotel.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String reservationKey;
    private String hotelName;
    private String roomTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private int numberOfRooms; //예약객실수
    private int totalPrice; //결제금액
    private PaymentStatus status;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAt;
    private String imageUrl;

    //reservation -> dto변환
    public static ReservationDetailResponse from(Reservation reservation){
        return ReservationDetailResponse.builder()
                .reservationKey(reservation.getReservationKey())
                .hotelName(reservation.getHotel().getName())
                .imageUrl(reservation.getRoomType().getImageUrl())
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
