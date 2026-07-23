package com.hotel.admin.service;

import com.hotel.admin.dto.AdminReservationSearchResponse;
import com.hotel.reservation.domain.ReservationSearchType;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminReservationService {
    private final ReservationRepository reservationRepository;

    public Page<AdminReservationSearchResponse> getReservations(LocalDate startDate, LocalDate endDate, ReservationSearchType searchType, String keyword, ReservationStatus status,Boolean roomAssigned, Pageable pageable) {

        Page<Reservation> result = reservationRepository.searchByReservation(startDate, endDate, searchType, keyword, status, roomAssigned,  pageable);

        return result.map(AdminReservationSearchResponse::from);
    }
}
