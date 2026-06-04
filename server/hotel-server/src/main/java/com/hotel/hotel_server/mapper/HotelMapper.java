package com.hotel.hotel_server.mapper;

import com.hotel.hotel_server.domain.Hotel;
import com.hotel.hotel_server.dto.HotelSearchParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotelMapper {
    List<Hotel> findByHotelFilter(HotelSearchParam param);
}
