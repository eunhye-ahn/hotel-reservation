package com.hotel.reservation.domain;

import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//예약

@Entity
@Table(name="room_type_inventory")
@Getter
@NoArgsConstructor
public class RoomTypeInventory{
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

    //잔여객실수
    public int getAvailableCount(){
        return totalInventory - totalReserved;
    }

    //예약가능여부(10프로 초과예약포함)
    public boolean isAvailable(int numberOfRoomsToReserve){
        return (totalReserved + numberOfRoomsToReserve) <= (int)(totalInventory*1.1);
    }

    //예약 유효성검사 + 동기화? 라고하나
    public void reserve(int numberOfRoomsToReserve){
        if(!isAvailable(numberOfRoomsToReserve)){
            throw new CustomException(ErrorCode.RESERVATION_UNAVAILABLE);
        }
        this.totalReserved += numberOfRoomsToReserve;
    }
}
