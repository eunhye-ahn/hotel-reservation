package com.hotel.hotel.domain;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

//예약

@Entity
@Table(name="room_type_inventory")
@Getter
@NoArgsConstructor
@Slf4j
public class RoomTypeInventory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version //낙관적 락
    private Long version = 0L;

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
        boolean result = (totalReserved + numberOfRoomsToReserve) <= (int)(totalInventory*1.1);
        log.info("[isAvailable] id={}, version={}, totalReserved={}, 요청={}, totalInventory={}, 판정={}",
                id, version, totalReserved, numberOfRoomsToReserve, totalInventory, result);
        return (totalReserved + numberOfRoomsToReserve) <= (int)(totalInventory*1.1);
    }

    //예약가능객실 존재여부확인 및 재고차감
    public void reserve(int numberOfRoomsToReserve){
        log.info("[reserve] 예외발생 - id={}, version={}, totalReserved={}", id, version, totalReserved);
        if(!isAvailable(numberOfRoomsToReserve)){
            throw new CustomException(ErrorCode.RESERVATION_UNAVAILABLE);
        }
        int before = this.totalReserved;
        this.totalReserved += numberOfRoomsToReserve;
        log.info("[reserve] 성공 - id={}, version={}, {} -> {}", id, version, before, this.totalReserved);
    }

    //재고복구
    public void restore(int numberOfRoomsToReserve){
        if(this.totalReserved - numberOfRoomsToReserve < 0){
            throw new CustomException(ErrorCode.INVALID_RESTORE);
        }
        this.totalReserved -= numberOfRoomsToReserve;
    }
}
