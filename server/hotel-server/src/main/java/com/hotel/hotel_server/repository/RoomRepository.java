package com.hotel.hotel_server.repository;

import com.hotel.hotel_server.domain.Hotel;
import com.hotel.hotel_server.domain.Room;
import com.hotel.hotel_server.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {
    boolean existsByNameAndRoomTypeAndHotel(String name, RoomType roomType, Hotel hotel);

    Optional<Room> findByIdAndRoomTypeIdAndHotelId(Long id, Long roomTypeId, Long hotelId);

    List<Room> findAllByRoomType(RoomType roomType);
}
