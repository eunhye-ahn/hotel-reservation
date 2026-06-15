package com.hotel.hotel.repository;

import com.hotel.hotel.domain.RoomType;
import com.hotel.hotel.domain.RoomTypeInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomTypeInventoryRepository extends JpaRepository<RoomTypeInventory, Long> {
    Optional<RoomTypeInventory> findByRoomTypeAndDate(RoomType roomType, LocalDate date);

    void deleteByRoomType(RoomType roomType);

    Optional<RoomTypeInventory> findByRoomTypeIdAndDate(Long roomTypeId, LocalDate date);

    List<RoomTypeInventory> findByRoomTypeIdAndDateBetween(Long roomTypeId, LocalDate startDate, LocalDate endDate);
}
