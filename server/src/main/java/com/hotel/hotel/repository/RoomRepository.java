package com.hotel.hotel.repository;

import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.Room;
import com.hotel.hotel.domain.RoomType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long>, RoomRepositoryCustom {

    List<Room> findAllByRoomType(RoomType roomType);

    @Query("SELECt DISTINCT r.floor FROM Room r WHERE r.roomType.hotel.id = :hotelId")
    List<Integer> findFloorsByOption(@Param("hotelId")Long hotelId);
}
