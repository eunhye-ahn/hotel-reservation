package com.hotel.hotel.repository;

import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.Room;
import com.hotel.hotel.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {
    boolean existsByNameAndRoomTypeAndHotel(String name, RoomType roomType, Hotel hotel);

    Optional<Room> findByIdAndRoomTypeIdAndHotelId(Long id, Long roomTypeId, Long hotelId);

    List<Room> findAllByRoomType(RoomType roomType);
}
