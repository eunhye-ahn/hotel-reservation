package com.hotel.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReservationRequest {
    private String reservationKey; //멱등키(프론트에서 생성) -UUID

    @NotBlank(message = "가격 토큰이 없습니다")
    private String priceToken;

    @NotNull(message = "호텔을 선택해주세요")
    private Long hotelId;

    @NotNull(message = "객실 유형을 선택해주세요")
    private Long roomTypeId;

    @NotNull(message = "체크인 날짜를 입력해주세요")
    @FutureOrPresent(message = "체크인 날짜는 오늘 이후여야 합니다")
    private LocalDate startDate;

    @NotNull(message = "체크아웃 날짜를 입력해주세요")
    @Future(message = "체크아웃 날짜는 오늘 이후여야 합니다")
    private LocalDate endDate;

    @Min(value = 1, message = "최소 1명 이상이어야 합니다")
    private int numberOfGuests;

    @Min(value = 1, message = "최소 1개 이상이어야 합니다")
    private int numberOfRooms;
}