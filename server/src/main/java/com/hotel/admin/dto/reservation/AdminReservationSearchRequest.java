package com.hotel.admin.dto.reservation;

import com.hotel.reservation.domain.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class AdminReservationSearchRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String hotelName;
    private String username;
    private String reservationKey;

    private ReservationStatus status;
    private int page;
}
