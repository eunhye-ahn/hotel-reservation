package com.hotel.hotel.repository;

import com.hotel.admin.dto.inventory.AdminRoomInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;


public interface RoomRepositoryCustom {
    Page<AdminRoomInfoResponse> searchByRoomInfo(Long hotelId, Long roomTypeId, Integer floor, LocalDate targetDate, Pageable pageable);
}
