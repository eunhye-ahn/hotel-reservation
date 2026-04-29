package com.hotel.reservation.service;

import com.hotel.reservation.domain.*;
import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.dto.ReservationDetailResponse;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    private final RateRepository rateRepository;
    private final ReservationRepository reservationRepository;

    //예약생성
    public ReservationDetailResponse createReservation(ReservationRequest request, Long userId){
        //엔티티조회-유효성검사
        Hotel hotel = hotelRepository
                .findById(request.getHotelId())
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));



        //예약가능여부 확인
        LocalDate date = request.getStartDate();
        RoomTypeInventory inventory = roomTypeInventoryRepository.findByRoomTypeAndDate(roomType, date)
                .orElseThrow(()->new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND));
        inventory.reserve(request.getNumberOfRoomsToReserve());

        //총금액계산
        Rate rate = rateRepository.findByRoomTypeAndDate(roomType, date)
                .orElseThrow(()-> new CustomException(ErrorCode.RATE_NOT_FOUND));
        int nights = (int)(ChronoUnit.DAYS.between(request.getStartDate(),request.getEndDate()));
        int totalPrice = nights*request.getNumberOfRoomsToReserve()*rate.getDemandRate();

        //예약생성
        Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .roomType(roomType)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfRooms(request.getNumberOfRoomsToReserve())
                .totalPrice(totalPrice)
                .paymentStatus(PaymentStatus.PAID)
                .reservationStatus(ReservationStatus.BEFORE_USE)
                .build();

        return ReservationDetailResponse.from(reservationRepository.save(reservation));
    }

    //내 예약조회
    public List<ReservationResponse> getMyReservations(Long userId){
        //엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

        //내예약조회
        return reservationRepository.findByUser(user)
                .stream().map(ReservationResponse::from)
                .toList();
    }

    //전체예약조회 -관리자
    public List<ReservationResponse> getReservations(){

        return reservationRepository.findAll()
                .stream().map(ReservationResponse::from)
                .toList();
    }
}
