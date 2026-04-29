package com.hotel.reservation.domain;

import com.hotel.reservation.dto.HotelUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//체크인

@Entity
@Table(name="room")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Room extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int floor;

    @Column
    private int number;

    @Column
    private String name;

    @Column(nullable = false)
    private boolean is_available;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    public void update(String name, Integer floor, Integer number, Boolean is_available){
        if (name != null) this.name = name;
        if (floor != null) this.floor = floor;
        if (number != null) this.number = number;
        if (is_available != null) this.is_available = is_available;
    }
}
