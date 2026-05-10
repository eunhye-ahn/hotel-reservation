package com.hotel.reservation.domain;

import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Table(name="reservation")
@Check(constraints = "start_date < end_date")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Reservation extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * [WHAT]
     * 멱등키 - 같은요청이 여러번 와도 딱 한번만 처리하도록 하는 키
     * [WHY]
     *
     * [흐름]
     * 클라이언트가 reservationKey(멱등키) 생성
     * > POST /v1/reservations 요청
     * > 서버: Redis에서 reservationKey 조회
     * > ┌─── 없음 → 처리 진행 → 결과 Redis에 저장 → 응답
     *   └─── 있음 → Redis에서 이전 결과 꺼내서 → 그대로 응답
     *
     * [Redis 저장 구조]
     *   key   : idempotency:{reservationId}
     * value : {
     *   status   : "processing" | "completed" | "failed",
     *              ------------409 ---------- 200
     *   result   : true/false,
     *   userId   : 123,          // 다른 유저가 같은 키 쓰는 거 방지 - 다르면 403
     *   request  : { ... },      // 요청 본문 해시 (변조 감지용) - 본문과 다르면 422
     *   createdAt: "2026-04-27T..."
     * }
     * TTL   : 24시간 (또는 예약 정책에 맞게)
     *
     * [Redis SET NX]
     * NX = "Not eXists" -> 키가 없을때만 저장, 있으면 무시
     * # 일반 SET → 무조건 덮어씀
     * SET foo "hello"   # 저장됨
     * SET foo "world"   # 덮어씀 → foo = "world"
     *
     * # SET NX → 없을 때만 저장
     * SET foo "hello" NX   # 저장됨 (키 없었으니까) → OK
     * SET foo "world" NX   # 무시됨 (키 있으니까)   → nil
     *
     * [왜 동시성이 해결되냐?]
     * 요청A ──┐
     *         ├──→ SET idempotency:abc "processing" NX
     * 요청B ──┘
     *
     * 결과:
     * 요청A → OK  (내가 먼저 선점!) → 예약 처리 진행
     * 요청B → nil (이미 있음)      → 409 반환
     * redis는 싱글스레드라 set nx가 원자적으로 실행됨
     *
     *
     */
    //동시성제어하는 멱등키 + 외부식별자겸 - pathvariable
    @Column(unique = true, name = "reservation_key")
    private String reservationKey; //멱등키 -클라에서UUID로 생성

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(nullable = false, name = "total_price")
    private int totalPrice; // 예약 당시 확정 금액

    @Column(nullable = false, name = "number_of_rooms")
    private int numberOfRooms; // 예약 객실 수

    @Column(nullable = false, name = "number_of_guests")
    private int numberOfGuests;

    /**
     * 역정규화
     * room_type으로 예약 후,
     * room은 체크인 시 배정
     */
    @Builder.Default
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType = null;

    @Builder.Default
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    private Room room = null;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public void cancel(){
        if(this.reservationStatus != ReservationStatus.BEFORE_USE){
            throw new CustomException(ErrorCode.CANNOT_CANCEL_RESERVATION);
        }

        this.reservationStatus = ReservationStatus.CANCELED;
        this.paymentStatus = PaymentStatus.REFUNDED;
    }
}
