package com.hotel.hotel_server.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HotelSearchParam {
    private String lDongRegnCd; //시도코드
    private String lDongSignguCd;   //시군구코드
    //private String lclsSystm2;  //숙박유형코드
    private LocalDate startDate;
    private LocalDate endDate;
    //private int totalDatys;     //모든날짜에 재고가 있는 호텔 필터용도(서비스단에서 계산)
    private int numberOfGuests;
    private int numberOfRooms;
    private LocalDate today;
    //private boolean available;
    private Long cursorId;
    private int size;
}
