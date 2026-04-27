package com.hotel.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//예약

@Entity
@Table(name="room_type_inventory")
@Getter
@NoArgsConstructor
public class RoomTypeInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false, name = "total_reserved")
    private int totalReserved;

    @Column(nullable = false, name = "total_inventory")
    private int totalInventory;
}
