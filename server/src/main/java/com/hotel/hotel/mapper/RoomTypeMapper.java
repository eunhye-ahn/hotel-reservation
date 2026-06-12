package com.hotel.hotel.mapper;

import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.RoomType;
import com.hotel.hotel.dto.HotelSearchParam;

import java.util.List;

public interface RoomTypeMapper {
    List<RoomType> findByRoomTypeFilter(RoomTypeInventoryParam param);
}
