package com.hotel.hotel.repository;

import com.hotel.hotel.domain.RoomTypeInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomTypeInventoryRepository extends JpaRepository<RoomTypeInventory, Long> {
    Optional<RoomTypeInventory> findByRoomTypeIdAndDate(Long roomTypeId, LocalDate date);

    List<RoomTypeInventory> findByRoomTypeIdAndDateBetweenOrderByDateAsc(Long roomTypeId, LocalDate startDate, LocalDate endDate);
}
