package com.hotel.reservation.service;

import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.domain.Rate;
import com.hotel.hotel.domain.RoomType;
import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.hotel.repository.RateRepository;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.hotel.repository.RoomTypeRepository;
import com.hotel.reservation.domain.PaymentStatus;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.domain.User;
import com.hotel.reservation.dto.ReservationRequest;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationTransactionService {

    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    private final ReservationRepository reservationRepository;
    private final RateRepository rateRepository;

    //실제 예약 생성
    @Transactional
    public void createReservationInTransaction(ReservationRequest request, Long userId){
        //멱등키검사
        if (reservationRepository.existsByReservationKey(request.getReservationKey())) {
            return;
        }

        //엔티티조회-유효성검사
        Hotel hotel = hotelRepository
                .findById(request.getHotelId())
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //예약날짜 서비스레벨 차단(외 @Valid 애플리케이션레벨 차단 / DB제약조건 DB레벨 차단)
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }
        //최대인원수 검사
        if (request.getNumberOfGuests() > roomType.getMaxOccupancy() * request.getNumberOfRooms()) {
            throw new CustomException(ErrorCode.EXCEED_MAX_OCCUPANCY);
        }

        //기간합산 재고 조회
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository
                .findByRoomTypeIdAndDateBetween(roomType.getId(), request.getStartDate(), request.getEndDate().minusDays(1));

        //날짜별 요금조회
        List<Rate> rates = rateRepository.findByRoomTypeIdAndDateBetween(request.getRoomTypeId(), request.getStartDate(), request.getEndDate().minusDays(1));

        //날짜수 검증 -요금누락검사
        long expectedDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (rates.size() != expectedDays) {
            throw new CustomException(ErrorCode.RATE_NOT_FOUND);
        }
        if (inventories.size() != expectedDays) {
            throw new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND);
        }

        //가격계산
        int totalDemandRate = rates.stream().mapToInt(Rate::getDemandRate).sum();
        int totalPrice = totalDemandRate * request.getNumberOfRooms();

        //재고 확인 및 차감
        for (RoomTypeInventory inventory : inventories) {
            inventory.reserve(request.getNumberOfRooms());
        }

        //orderId 생성 -분산트랜잭션(예약-결제) 식별키
        String orderId = UUID.randomUUID().toString();

        //예약생성
        Reservation reservation = Reservation.builder()
                .displayReservationNO(generateDisplayReservationNo())
                .reservationKey(request.getReservationKey())
                .orderId(orderId)
                .hotel(hotel)
                .roomType(roomType)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfRooms(request.getNumberOfRooms())
                .numberOfGuests(request.getNumberOfGuests())
                .totalPrice(totalPrice)
                .paymentStatus(PaymentStatus.PENDING)
                .reservationStatus(ReservationStatus.BEFORE_USE)
                .build();

        reservationRepository.save(reservation);
    }

    private String generateDisplayReservationNo(){
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String shortId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "RESERVE-"+datePart+"-"+shortId;
    }
}
