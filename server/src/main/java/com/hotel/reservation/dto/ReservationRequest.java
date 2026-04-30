package com.hotel.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private String reservationId; //멱등키(프론트에서 생성) -UUID
    private Long hotelId;
    private Long roomTypeId;

    @NotNull(message = "체크인 날짜를 입력해주세요")
    @FutureOrPresent(message = "체크인 날짜는 오늘 이후여야 합니다")
    private LocalDate startDate;

    @NotNull(message = "체크아웃 날짜를 입력해주세요")
    @Future(message = "체크아웃 날짜는 오늘 이후여야 합니다")
    private LocalDate endDate;
    private int numberOfRoomsToReserve;
}