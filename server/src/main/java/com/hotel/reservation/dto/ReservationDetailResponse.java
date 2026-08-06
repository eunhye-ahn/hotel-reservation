package com.hotel.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hotel.reservation.domain.CancelType;
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
    private PaymentStatus paymentStatus;
    private ReservationStatus reservationStatus;
    private String displayReservationNO;
    private CancelType cancelType;
    private String cancelReason;

    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAt;
    private String roomTypeImageUrl;
    private  String hotelImageUrl;

    public static ReservationDetailResponse from(Reservation reservation){
        return ReservationDetailResponse.builder()
                .reservationKey(reservation.getReservationKey())
                .hotelName(reservation.getHotel().getName())
                .roomTypeImageUrl(reservation.getRoomType().getImageUrl())
                .hotelImageUrl(reservation.getHotel().getImageUrl())
                .roomTypeName(reservation.getRoomType().getName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .checkInTime(reservation.getHotel().getCheckInTime())
                .checkOutTime(reservation.getHotel().getCheckOutTime())
                .numberOfRooms(reservation.getNumberOfRooms())
                .totalPrice(reservation.getTotalPrice())
                .reservationStatus(reservation.getReservationStatus())
                .paymentStatus(reservation.getPaymentStatus())
                .createdAt(reservation.getCreatedAt())
                .displayReservationNO(reservation.getDisplayReservationNO())
                .cancelType(reservation.getCancelType())
                .cancelReason(reservation.getCancelReason())
                .build();
    }
}
