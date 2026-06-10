package com.hotel.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationCreateResponse {
    private String reservationKey;
    private String orderId;
}
