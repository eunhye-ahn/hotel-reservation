package com.hotel.admin.dto;

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
public class AdminReservationDetailResponse {
    private Long reservationId;
    private String reservationKey;
    private String username;
    private int numberOfGuests;
    private int numberOfRooms;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private int totalPrice;
    private PaymentStatus paymentStatus;
    private ReservationStatus reservationStatus;
    private String hotelName;
    private String roomTypeName;
    private boolean roomAssigned;
    private Integer roomNumber;
    private Integer floor;
    private String roomName;
    private Boolean usable;



    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime createdAt;

    public static AdminReservationDetailResponse from(Reservation reservation){
        return AdminReservationDetailResponse.builder()
                .reservationId(reservation.getId())
                .reservationKey(reservation.getReservationKey())
                .username(reservation.getUser().getName())
                .numberOfGuests(reservation.getNumberOfGuests())
                .numberOfRooms(reservation.getNumberOfRooms())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .checkInTime(reservation.getHotel().getCheckInTime())
                .checkOutTime(reservation.getHotel().getCheckOutTime())
                .totalPrice(reservation.getTotalPrice())
                .paymentStatus(reservation.getPaymentStatus())
                .reservationStatus(reservation.getReservationStatus())
                .hotelName(reservation.getHotel().getName())
                .roomTypeName(reservation.getRoomType().getName())
                .createdAt(reservation.getCreatedAt())
                .roomAssigned(reservation.getRoom() != null)
                .roomNumber(reservation.getRoom() != null ? reservation.getRoom().getNumber() : null)
                .floor(reservation.getRoom() != null ? reservation.getRoom().getFloor() : null)
                .roomName(reservation.getRoom() != null ? reservation.getRoom().getName() : null)
                .usable(reservation.getRoom() != null ? reservation.getRoom().isUsable(): null)
                .build();
    }
}
