package com.hotel.reservation.repository;

import com.hotel.hotel.domain.ReservationSearchType;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationRepositoryCustom {
    Page<Reservation> searchByReservation(LocalDate startDate, LocalDate endDate, ReservationSearchType searchType, String keyword, ReservationStatus status, Pageable pageable);
}
