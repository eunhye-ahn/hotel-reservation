package com.hotel.admin.dto.reservation;

import com.hotel.reservation.domain.ReservationSearchType;
import com.hotel.reservation.domain.ReservationStatus;
import java.time.LocalDate;

public record AdminReservationSearchRequest (
    LocalDate startDate,
    LocalDate endDate,
    ReservationSearchType searchType,
    String keyword,
    ReservationStatus status,
    Boolean roomAssigned,
    int page
){}
