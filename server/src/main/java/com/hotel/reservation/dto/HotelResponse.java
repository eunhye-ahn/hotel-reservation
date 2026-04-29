package com.hotel.reservation.dto;

import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.domain.Rate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class HotelResponse {
    private Long hotelId;
    private String name;
    private int maxRate;
    private int demandRate; //최저가
    private int discountRate;
    private LocalTime checkInTime;
    private String address;
    private String imageUrl;

    //정적팩토리메서드
    /**
     * 호텔 목록 조회 응답 dto
     *
     * 호텔 기본 정보 + 오늘 날짜 기준 가장 저렴한 객실 타입의 요금 정보를 반환
     * -maxRate : 정가 (취소선 표시용)
     * -demandRate : 현재가 (수요 기반 자동계산)
     * -discountRate : 할인율 (maxRate 기준으로 서버에서 계산)
     *
     * Rate 없는 호텔은 목록에서 제외 (repository 단에서 필터링)
     * 같은 roomType 기준으로 demandRate가 가장 낮은 타입 선택
     *
     */
    public static HotelResponse from(Hotel hotel, Rate cheapestRate){
        int discountRate = (int) Math.round(
                (double)(cheapestRate.getMaxRate() - cheapestRate.getDemandRate())/cheapestRate.getMaxRate() * 100
        );

        return HotelResponse.builder()
                .hotelId(hotel.getId())
                .name(hotel.getName())
                .maxRate(cheapestRate.getMaxRate())
                .demandRate(cheapestRate.getDemandRate())
                .discountRate(discountRate)
                .checkInTime(hotel.getCheckInTime())
                .address(hotel.getAddress())
                .imageUrl(hotel.getImageUrl())
                .build();
    }
}
