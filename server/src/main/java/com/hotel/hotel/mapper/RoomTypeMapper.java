package com.hotel.hotel.mapper;

import com.hotel.hotel.dto.RoomTypeInventoryParam;
import com.hotel.hotel.dto.RoomTypeResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomTypeMapper {
    List<RoomTypeResponse> findByRoomTypeFilter(RoomTypeInventoryParam param);
}
