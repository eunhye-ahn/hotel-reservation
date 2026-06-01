package com.hotel.hotel_server.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotelSearchRequest {
    private String q;
    private String lDongRegnCd; //시도코드
    private String lDongSignguCd;   //시군구코드
    private String lclsSystm2;  //숙박유형코드
    private double latitude;
    private double logitude;
    private String cursor;
    @Builder.Default
    private int size = 10;
}
