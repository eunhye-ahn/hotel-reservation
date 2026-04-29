package com.hotel.reservation.repository;

import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.domain.Room;
import com.hotel.reservation.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {
    boolean existsByNameAndRoomTypeAndHotel(String name, RoomType roomType, Hotel hotel);

    Optional<Room> findByIdAndRoomTypeIdAndHotelId(Long id, Long roomTypeId, Long hotelId);
}
