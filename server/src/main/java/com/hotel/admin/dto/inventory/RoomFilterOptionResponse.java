package com.hotel.admin.dto.inventory;

import com.hotel.hotel.domain.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


public record RoomFilterOptionResponse (
     List<Integer> floors,
     List<RoomTypeOption> roomTypes
){
    @Getter
    @AllArgsConstructor
    public static class RoomTypeOption {
        private Long id;
        private String name;

        public static RoomTypeOption from(RoomType roomType) {
            return new RoomTypeOption(roomType.getId(), roomType.getName());
        }
    }
}
