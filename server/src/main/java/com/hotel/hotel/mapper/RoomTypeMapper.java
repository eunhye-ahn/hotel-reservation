package com.hotel.hotel.mapper;

import com.hotel.cart.dto.CartRoomTypeParam;
import com.hotel.hotel.dto.RoomTypeInventoryParam;
import com.hotel.hotel.dto.RoomTypeResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomTypeMapper {
    //룸타입 필터
    List<RoomTypeResponse> findByRoomTypeFilter(RoomTypeInventoryParam param);
}
