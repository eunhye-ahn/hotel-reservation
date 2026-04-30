package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Room;
import com.hotel.reservation.domain.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomTypeDetailResponse {
    private Long roomTypeId;
    private String roomTypeName;
    private int maxOccupancy;
    private String imageUrl;
    private List<RoomInfoResponse> rooms;

    public static RoomTypeDetailResponse from(RoomType roomType, List<Room> rooms){
        return RoomTypeDetailResponse.builder()
                .roomTypeId(roomType.getId())
                .roomTypeName(roomType.getName())
                .maxOccupancy(roomType.getMaxOccupancy())
                .imageUrl(roomType.getImageUrl())
                .rooms(rooms.stream()
                        .map(RoomInfoResponse::from)
                        .toList())
                .build();
    }
}
