package com.hotel.hotel.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class HotelSearchParam {
    private List<Long> hotelIds;    //텍스트 검색 결과
    private String lDongRegnCd; //시도코드
    private String lDongSignguCd;  //시군구코드
    private String lclsSystm2;  //숙박유형코드
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfGuests;
    private Integer numberOfRooms;
    private LocalDate today;
    private int totalDays;      //모든날짜에 재고가 있는 호텔 필터용도(서비스단에서 계산)
    //private boolean available;
    private Long cursorId;
    private int size;
}
