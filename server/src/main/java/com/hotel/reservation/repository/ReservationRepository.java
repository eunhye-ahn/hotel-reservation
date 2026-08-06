package com.hotel.reservation.repository;

import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,ReservationRepositoryCustom {
    Optional<Reservation> findByUserIdAndReservationKey(Long userId, String reservationKey);

    Page<Reservation> findByUserAndReservationStatusOrderByCreatedAtDesc(User user, ReservationStatus reservationStatus, Pageable pageable);

    Optional<Reservation> findByReservationKey(String reservationKey);

    boolean existsByReservationKey(String reservationKey);

    List<Reservation> findByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime limitTime);

    @Query("SELECT COUNT(r.id) FROM Reservation r " +
            "WHERE r.startDate = :startDate " +
            "AND r.reservationStatus = :reservationStatus")
    int countCheckInByDate(@Param("startDate") LocalDate startDate,@Param("reservationStatus") ReservationStatus reservationStatus);

    @Query("SELECT COUNT(r.id) FROM Reservation r " +
            "WHERE r.reservationStatus = :reservationStatus " +
            "AND r.room is null")
    int countUnassigned(@Param("reservationStatus")ReservationStatus reservationStatus);

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.hotel " +
            "WHERE r.reservationStatus = :status AND r.room IS NULL " +
            "ORDER BY r.startDate ASC")
    List<Reservation> findUnassignedPreview(
            @Param("status") ReservationStatus status,
            Pageable pageable
    );

    interface ReservationStatusCount {
        ReservationStatus getStatus();
        int getCount();
    }

    @Query("SELECT r.reservationStatus AS status, COUNT(r) AS count " +
            "FROM Reservation r " +
            "WHERE r.createdAt >= :start AND r.createdAt < :end " +
            "GROUP BY r.reservationStatus")
    List<ReservationStatusCount> countByStatusMonth(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);

    //예약 체크아웃처리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r " +
            "SET r.reservationStatus = :newStatus " +
            "WHERE r.reservationStatus = :oldStatus " +
            "AND r.startDate < :today")
    int markCompletedAfterCheckOut(
            @Param("oldStatus") ReservationStatus oldStatus,
            @Param("newStatus") ReservationStatus newStatus,
            @Param("today") LocalDate today
    );
}
