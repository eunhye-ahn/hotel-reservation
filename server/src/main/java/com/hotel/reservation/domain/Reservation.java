package com.hotel.reservation.domain;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.Room;
import com.hotel.hotel.domain.RoomType;
import com.hotel.user.domain.User;
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

    @Column(name="display_reservation_no",unique = true)
    private String displayReservationNO;

    @Column(nullable = false)
    private String orderId;

    //동시성제어하는 멱등키 + 외부식별자겸 - pathvariable
    @Column(unique = true, name = "reservation_key")
    private String reservationKey; //멱등키 -클라에서UUID로 생성

    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

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
     * room은 관리자가 배정
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

    @Column(name="cancel_type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CancelType cancelType=null;

    @Builder.Default
    private String cancelReason=null;

    //결제 전 고객취소
    public void cancelByUser(){
        if(this.reservationStatus != ReservationStatus.BEFORE_USE){
            throw new CustomException(ErrorCode.CANNOT_CANCEL_RESERVATION);
        }
        if(this.paymentStatus != PaymentStatus.PENDING){
            throw new CustomException(ErrorCode.CANNOT_CANCEL_RESERVATION);
        }
        if(this.room != null){
            throw new CustomException(ErrorCode.CANNOT_CANCEL_RESERVATION);
        }
        this.reservationStatus = ReservationStatus.CANCELED;
        this.cancelType = CancelType.USER;
    }

    //결제시간 만료시, 예약만료 및 재고복구
    public void updateReservationExpire(){
        this.reservationStatus = ReservationStatus.EXPIRED;
    }

    //결제
    public void paid(){
        this.reservationStatus=ReservationStatus.BEFORE_USE;
        this.paymentStatus = PaymentStatus.PAID;
    }

    //객실배정
    public void assignRoom(Room room) {
        if (this.reservationStatus != ReservationStatus.BEFORE_USE) {
            throw new CustomException(ErrorCode.CANNOT_ASSIGN_ROOM);
        }
        this.room = room;
    }

    //객실배정취소
    public void unassignRoom(){
        if(this.reservationStatus != ReservationStatus.BEFORE_USE) {
            throw new CustomException(ErrorCode.CANNOT_UNASSIGN_ROOM);
        }
        this.room = null;
    }

    //예약취소 - 관리자
    public void cancelByAdmin(String reason){
        if(this.reservationStatus != ReservationStatus.BEFORE_USE) {
            throw new CustomException(ErrorCode.CANNOT_CANCEL_RESERVATION);
        }

        this.reservationStatus = ReservationStatus.CANCEL_PENDING;
        this.cancelType = CancelType.ADMIN;
        this.cancelReason = reason;
        this.room = null;
    }

    //결제 취소 성공
    public void completeCancel(){
        if(this.reservationStatus != ReservationStatus.CANCEL_PENDING) {
            throw new CustomException(ErrorCode.CANNOT_REFUND_RESERVATION);
        }
        this.reservationStatus = ReservationStatus.CANCELED;
    }

    public void refunded(){
        this.paymentStatus = PaymentStatus.REFUNDED;
    }
}
