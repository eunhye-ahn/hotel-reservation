package com.hotel.reservation.repository;

import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User User);

    Optional<Reservation> findByIdAndUserId(Long id, Long userId);

    Optional<Reservation> findByUserIdAndReservationKey(Long userId, String reservationKey);

    List<Reservation> findByUserAndReservationStatus(User user, ReservationStatus reservationStatus);
}
