package com.hotel.admin.dto.dashboard;


public record TopPendingBalanceHotel (
     long hotelId,
     String hotelName,
     long pendingBalance
){}
