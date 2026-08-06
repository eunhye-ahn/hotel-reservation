package com.hotel.admin.dto.dashboard;

import com.hotel.reservation.domain.Reservation;

import java.time.LocalDate;


public record UnassignRoomInfo (
     Long reservationId,
     String displayReservationNo,
     String hotelName,
     LocalDate startDate
){
    public static UnassignRoomInfo from(Reservation reservation){
        return new UnassignRoomInfo(
                reservation.getId(),
                reservation.getDisplayReservationNO(),
                reservation.getHotel().getName(),
                reservation.getStartDate()
        );
    }
}
