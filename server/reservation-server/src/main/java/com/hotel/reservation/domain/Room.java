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

    @Builder.Default
    @Column(nullable = false, name = "is_usable")
    private boolean usable = true;       //객실 사용 가능여부(공사중, 고장 등)

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    public void update(String name, Integer floor, Integer number, Boolean usable){
        if (name != null) this.name = name;
        if (floor != null) this.floor = floor;
        if (number != null) this.number = number;
        if (usable != null) this.usable = usable;
    }
}
