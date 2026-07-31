package com.hotel.admin.dto.dashboard;

import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReserveStatusStaticResponse {
    private ReservationStatus status;
    private int count;
}
