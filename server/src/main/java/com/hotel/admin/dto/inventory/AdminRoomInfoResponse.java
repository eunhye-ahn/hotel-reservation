package com.hotel.admin.dto.inventory;

public record AdminRoomInfoResponse (
     Long roomId,
     String roomName,
     int floor,
     int roomNumber,
     String roomTypeName,
     boolean usable,
     boolean assignable
){

}
