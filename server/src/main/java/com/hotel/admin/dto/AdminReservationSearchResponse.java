package com.hotel.admin.dto;

import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AdminReservationSearchResponse {
    private Long id;
    private String reservationKey;
    private String username;
    private String hotelName;
    private String roomTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus reservationStatus;
    private boolean roomAssigned;
    private LocalDate createdDate;

    public static AdminReservationSearchResponse from(Reservation r){
        return AdminReservationSearchResponse.builder()
                .id(r.getId())
                .reservationKey(r.getReservationKey())
                .username(r.getUser().getName())
                .hotelName(r.getHotel().getName())
                .roomTypeName(r.getRoomType().getName())
                .startDate(r.getStartDate())
                .endDate(r.getEndDate())
                .reservationStatus(r.getReservationStatus())
                .roomAssigned(r.getRoom() != null)
                .createdDate(r.getCreatedAt().toLocalDate())
                .build();
    }
}
