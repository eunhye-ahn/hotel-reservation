package com.hotel.hotel_server.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
public class HotelDocument {
    @Id
    private String id;

    @Field(type= FieldType.Long)
    private Long hotelId; //보류

    @Field(type=FieldType.Search_As_You_Type, analyzer = "nori_analyzer")
    private String hotelName;

    @Field(type=FieldType.Text, analyzer = "nori_analyzer")
    private String address;

    //시도 파싱 필요 <<필터링을 위해 keyword

    @Field
}
