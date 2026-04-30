package com.hotel.reservation.repository;

import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.domain.Room;
import com.hotel.reservation.domain.RoomType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    List<RoomType> findByHotelId(Long hotelId);

    boolean existsByNameAndHotel(String name, Hotel hotel);

    Optional<RoomType> findByIdAndHotelId(Long id, Long hotelId);
}