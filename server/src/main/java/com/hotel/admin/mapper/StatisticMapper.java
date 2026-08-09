package com.hotel.admin.mapper;

import com.hotel.admin.dto.dashboard.DailyStatisticsResponse;
import com.hotel.admin.dto.dashboard.UnassignRoomInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticMapper {
    List<DailyStatisticsResponse> getDailyStatistics();
}
