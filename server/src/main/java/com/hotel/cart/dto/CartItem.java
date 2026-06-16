package com.hotel.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String cartItemId;
    private Long roomTypeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private LocalDateTime createdAt;
}
