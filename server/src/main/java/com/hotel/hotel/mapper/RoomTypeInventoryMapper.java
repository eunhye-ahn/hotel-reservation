package com.hotel.hotel.mapper;

import com.hotel.admin.dto.inventory.AdminInventorySummaryResponse;
import com.hotel.admin.dto.inventory.RoomTypeInventoryCalendarResponse;
import com.hotel.hotel.domain.RoomTypeInventorySortType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RoomTypeInventoryMapper {
    List<AdminInventorySummaryResponse> searchInventorySummary
            (@Param("date") LocalDate date,
             @Param("hotelName")String hotelName,
             @Param("sortType")RoomTypeInventorySortType sortType,
             @Param("size")int size,
             @Param("offset")int offset);

    long countInventorySummary(@Param("hotelName")String hotelName);

    List<RoomTypeInventoryCalendarResponse> getInventoryCalendar(@Param("hotelId")Long hotelId,
                                                                 @Param("startDate")LocalDate startDate,
                                                                 @Param("endDate")LocalDate endDate);
}
