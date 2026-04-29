package com.hotel.reservation.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [예약 흐름]
 1. 호텔 목록 조회
 → hotel 테이블

 2. 객실 유형 + 가격 + 잔여객실 조회
 → room_type JOIN room_type_inventory
 → "디럭스룸 / 20만원 / 잔여 3개"

 3. 예약 가능 여부 확인
 → room_type_inventory
 → total_reserved + 1 <= total_inventory

 4. 예약 확정
 → reservation 생성
 → room_type_inventory total_reserved +1

 [체크인 흐름]
 5. 실제 방 배정
 → room 테이블에서 is_available = true인 방 찾아서 배정
 → reservation.room_id 업데이트
 */

@Entity
@Table(name="room_type")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RoomType extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "max_occupancy")
    private int maxOccupancy;

    @Column(name = "image_url")
    private String imageUrl;

    public void update(String name, Integer maxOccupancy, String imageUrl){
        if (name != null) this.name = name;
        if (maxOccupancy != null) this.maxOccupancy = maxOccupancy;
        if (imageUrl != null) this.imageUrl = imageUrl;
    }
}
