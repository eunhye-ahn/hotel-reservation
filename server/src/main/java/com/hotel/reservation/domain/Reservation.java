package com.hotel.reservation.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="reservation")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Reservation extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true, name = "reservation_key")
//    private String reservationKey; //멱등키 -클라에서UUID로 생성

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(nullable = false, name = "total_price")
    private int totalPrice; // 예약 당시 확정 금액

    @Column(nullable = false, name = "number_of_rooms")
    private int numberOfRooms; // 예약 객실 수

    /**
     * 역정규화
     * room_type으로 예약 후,
     * room은 체크인 시 배정
     */
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Builder.Default
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    private Room room = null;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}
