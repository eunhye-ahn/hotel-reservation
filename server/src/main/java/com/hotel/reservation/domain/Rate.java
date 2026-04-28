package com.hotel.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="rate")
@Getter
@NoArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "base_rate")
    private int baseRate;          //기본가격

    @Column(nullable = false, name = "max_rate")
    private int maxRate;           //최대가격

    @Column(nullable = false, name = "demand_rate")
    private int demandRate;        //현재가격

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;
}
