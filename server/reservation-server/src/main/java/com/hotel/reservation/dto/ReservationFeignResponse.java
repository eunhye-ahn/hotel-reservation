package com.hotel.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationFeignResponse {
    private Long reservationId;
    private String reservationKey;
    private String paymentStatus;
    private int amount;
    private String sellerAccount;
    private Long userId;
}
