package com.hotel.admin.dto.dashboard;

import com.hotel.reservation.domain.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class UnassignRoomInfo {
    private Long reservationId;
    private String displayReservationNo;
    private String hotelName;
    private LocalDate startDate;

    public static UnassignRoomInfo from(Reservation reservation){
        return UnassignRoomInfo.builder()
                .reservationId(reservation.getId())
                .displayReservationNo(reservation.getDisplayReservationNO())
                .hotelName(reservation.getHotel().getName())
                .startDate(reservation.getStartDate())
                .build();
    }
}
