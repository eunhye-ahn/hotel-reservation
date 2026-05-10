package com.hotel.reservation.repository;

import com.hotel.reservation.domain.RoomType;
import com.hotel.reservation.domain.RoomTypeInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomTypeInventoryRepository extends JpaRepository<RoomTypeInventory, Long> {
    Optional<RoomTypeInventory> findByRoomTypeAndDate(RoomType roomType, LocalDate date);

    void deleteByRoomType(RoomType roomType);

    Optional<RoomTypeInventory> findByRoomTypeIdAndDate(Long roomTypeId, LocalDate date);

    List<RoomTypeInventory> findByRoomTypeIdAndDateBetween(Long roomTypeId, LocalDate startDate, LocalDate endDate);
}
