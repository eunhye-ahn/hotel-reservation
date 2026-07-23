package com.hotel.reservation.repository;

import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,ReservationRepositoryCustom {
    Optional<Reservation> findByUserIdAndReservationKey(Long userId, String reservationKey);

    List<Reservation> findByUserAndReservationStatusOrderByCreatedAtDesc(User user, ReservationStatus reservationStatus);

    Optional<Reservation> findByReservationKey(String reservationKey);

    boolean existsByReservationKey(String reservationKey);

    List<Reservation> findByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime limitTime);
}
