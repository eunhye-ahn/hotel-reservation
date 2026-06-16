package com.hotel.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CartRequest {
    @NotNull
    private long roomTypeId;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;
}
