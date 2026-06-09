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
@Document(indexName = "hotels")
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

    public static HotelDocument from(Hotel hotel){
        return HotelDocument.builder()
                .id(hotel.getId().toString())
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .address(hotel.getAddress())
                .build();
    }
}