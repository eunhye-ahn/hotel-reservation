package com.hotel.reservation.repository;

import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.User;
import com.hotel.reservation.dto.ReservationResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
}
