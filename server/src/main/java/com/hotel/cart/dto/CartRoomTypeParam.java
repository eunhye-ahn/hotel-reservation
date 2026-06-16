package com.hotel.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CartRoomTypeParam {
    private Long roomTypeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int totalDays;
}
