package com.hotel.hotel.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/**
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *
 *     @Column(nullable = false)
 *     private String name;
 *
 *     @Column(nullable = false)
 *     private String address;
 *
 *     @Column(nullable = false, unique = true)
 *     private String sellerAccount;
 *
 *     @Column
 *     private Double latitude;   // 위도 (37.5665)
 *
 *     @Column
 *     private Double longitude;  // 경도 (126.9780)
 *
 *     @Column
 *     private String imageUrl;
 *
 *     @Column(nullable = false)
 *     private LocalTime checkInTime;
 *
 *     @Column(nullable = false)
 *     private LocalTime checkOutTime;
 */
//@Document(indexName = "hotels")
@Getter
@Builder
public class HotelDocument {
    @Id
    private String id;      //ES _id(content_id) -중복저장방지(덮어쓰기)+원본데이터 연결용

    @Field(type= FieldType.Long)
    private Long hotelId; //커서페이징 정렬보조키 + querydsl rdb 연결용

    @Field(type=FieldType.Search_As_You_Type, analyzer = "nori_analyzer")
    private String hotelName;   //자동완성 검색

    @Field(type=FieldType.Text, analyzer = "nori_analyzer")
    private String address; //부분 검색

    //시도 파싱 필요 <<필터링을 위해 keyword
    @Field(type = FieldType.Keyword)
    private String lDongRegnCd; //시도필터링

    @Field(type = FieldType.Keyword)
    private String lDongSignguCd;   //시군구 필터링

    @Field(type = FieldType.Keyword)
    private String lclsSystm2;      //숙박유형 필터링

    @GeoPointField
    private GeoPoint point;    //위치기반검색

    public static HotelDocument from(Hotel hotel){
        return HotelDocument.builder()
                .id(hotel.getContentId().toString())
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .address(hotel.getAddress())
                .lDongRegnCd(hotel.getLDongRegnCd())
                .lDongSignguCd(hotel.getLDongSignguCd())
                .lclsSystm2(hotel.getLclsSystm2())
                .point(hotel.getLatitude() != null && hotel.getLongitude() != null
                        ? new GeoPoint(hotel.getLatitude(), hotel.getLongitude())
                        : null)
                .build();

    }
}