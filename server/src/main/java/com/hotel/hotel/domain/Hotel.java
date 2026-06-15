package com.hotel.hotel.domain;

import com.hotel.common.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "hotel")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Hotel extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long contentId;   //원본데이터연결용(중복저장방지)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String sellerAccount;

    @Column
    private Double latitude;   // 위도 (37.5665)

    @Column
    private Double longitude;  // 경도 (126.9780)

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private LocalTime checkInTime;

    @Column(nullable = false)
    private LocalTime checkOutTime;

    @Column
    private String lDongRegnCd; //시도코드

    @Column
    private String lDongSignguCd;   //시군구코드

    @Column
    private String lclsSystm2;  //숙소유형코드

    public void update(String name, String address, Double latitude, Double longitude, String imageUrl, LocalTime checkInTime, LocalTime checkOutTime) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (latitude != null) this.latitude = latitude;
        if (longitude != null) this.longitude = longitude;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (checkInTime != null) this.checkInTime = checkInTime;
        if (checkOutTime != null) this.checkOutTime = checkOutTime;
    }
}
