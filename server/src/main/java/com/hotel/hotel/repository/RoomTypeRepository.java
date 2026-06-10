package com.hotel.hotel.repository;

import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    List<RoomType> findByHotelId(Long hotelId);

    boolean existsByNameAndHotel(String name, Hotel hotel);

    Optional<RoomType> findByIdAndHotelId(Long id, Long hotelId);
}