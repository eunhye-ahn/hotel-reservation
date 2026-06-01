package com.hotel.hotel_server.repository;

import com.hotel.hotel_server.domain.Hotel;

import java.util.List;

public interface HotelRepositoryCustom {
    List<Hotel> findByRegionWithCursor(String lDongRegnCd, String lDongSignguCd, Long cursorId, int size);
}
