package com.hotel.admin.dto.dashboard;

import com.hotel.reservation.domain.ReservationStatus;

public record ReserveStatusStaticResponse (
        ReservationStatus status,
        int count
){}
