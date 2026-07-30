package com.hotel.hotel.repository;

import com.hotel.admin.dto.inventory.AdminRoomInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface RoomRepositoryCustom {
    Page<AdminRoomInfoResponse> searchByRoomInfo(Long hotelId, Long roomTypeId, Integer floor, Pageable pageable);
}
