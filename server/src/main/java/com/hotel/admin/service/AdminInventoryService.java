package com.hotel.admin.service;

import com.hotel.admin.dto.inventory.AdminInventorySummaryResponse;
import com.hotel.admin.dto.inventory.AdminRoomInfoResponse;
import com.hotel.admin.dto.inventory.RoomFilterOptionResponse;
import com.hotel.admin.dto.inventory.RoomTypeInventoryCalendarResponse;
import com.hotel.hotel.domain.RoomType;
import com.hotel.hotel.domain.RoomTypeInventorySortType;
import com.hotel.hotel.mapper.RoomTypeInventoryMapper;
import com.hotel.hotel.repository.RoomRepository;
import com.hotel.hotel.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminInventoryService {
    private final RoomTypeInventoryMapper roomTypeInventoryMapper;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    //호텔 재고목록
    public Page<AdminInventorySummaryResponse> searchInventorySummary(
            LocalDate date, String hotelName, RoomTypeInventorySortType sortType, Pageable pageable) {
        List<AdminInventorySummaryResponse> content
                = roomTypeInventoryMapper.searchInventorySummary(date, hotelName, sortType, pageable.getPageSize(), (int)pageable.getOffset());
        long total = roomTypeInventoryMapper.countInventorySummary(hotelName);

        return new PageImpl<>(content, pageable, total);
    }

    public List<RoomTypeInventoryCalendarResponse> getInventoryCalendar(Long hotelId,
                                                                        LocalDate startDate,
                                                                        LocalDate endDate) {
        return roomTypeInventoryMapper.getInventoryCalendar(hotelId, startDate, endDate);
    }

    //호텔의 객실정보
    public Page<AdminRoomInfoResponse> searchByRoomInfo(Long hotelId, Long roomTypeId, Integer floor, Pageable pageable) {
        return roomRepository.searchByRoomInfo(hotelId, roomTypeId, floor, pageable);
    }

    //객실필터옵션
    public RoomFilterOptionResponse getFilterOptions(Long hotelId){
        List<Integer> floors = roomRepository.findFloorsByOption(hotelId);
        List<RoomFilterOptionResponse.RoomTypeOption> roomTypes =
                roomTypeRepository.findByHotelId(hotelId)
                        .stream()
                        .map(RoomFilterOptionResponse.RoomTypeOption::from)
                        .toList();

        return RoomFilterOptionResponse.builder()
                .floors(floors)
                .roomTypes(roomTypes)
                .build();
    }
}
