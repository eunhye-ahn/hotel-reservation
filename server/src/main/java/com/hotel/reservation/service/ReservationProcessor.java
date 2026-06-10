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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * 분리한 이유:
 * @Transactional, @Retryable은 프록시 기반으로 동작한다.
 * 같은 클래스 내부에서 메서드를 호출하면 프록시를 거치지 않아
 * 어노테이션이 무시된다.
 *
 * ReservationService.createReservation() 에서
 * 같은 클래스의 process()를 직접 호출하면
 * @Transactional, @Retryable 둘 다 동작하지 않는다.
 *
 * 따라서 별도의 빈으로 분리하여 외부에서 호출하도록 해
 * 프록시를 거치게 만든다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationProcessor {

//    private final PriceTokenRedisService priceTokenRedisService;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    private final ReservationRepository reservationRepository;
    private final RateRepository rateRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,   //이 예외발생하면
            maxAttempts = 3,                            //최대 3번 시도
            backoff = @Backoff(delay = 100)             //100ms 대기 후 재시도
    )
    public void processWithRetry(ReservationRequest request, Long userId){
        log.info("processor retry - reservationKey: {}", request.getReservationKey());
        //토큰검증
//        PriceTokenValue priceTokenValue = priceTokenRedisService.get(request.getPriceToken())
//                .orElseThrow(()-> new CustomException(ErrorCode.PRICE_TOKEN_NOT_FOUND));

//        int totalPrice = priceTokenValue.getTotalPrice();

        //멱등키검사(방어선)
        if(reservationRepository.existsByReservationKey(request.getReservationKey())){
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
        if(rates.size() != expectedDays) {
            throw new CustomException(ErrorCode.RATE_NOT_FOUND);
        }
        if (inventories.size() != expectedDays) {
            throw new CustomException(ErrorCode.ROOM_INVENTORY_NOT_FOUND);
        }

        //가격계산
        int totalDemandRate = rates.stream().mapToInt(Rate::getDemandRate).sum();
        int totalPrice = totalDemandRate * request.getNumberOfRooms();

        //재고 확인 및 차감
        for(RoomTypeInventory inventory : inventories){
            inventory.reserve(request.getNumberOfRooms());
        }

        //orderId 생성 -분산트랜잭션(예약-결제) 식별키
        String orderId = UUID.randomUUID().toString();

        //예약생성
        Reservation reservation = Reservation.builder()
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

        //커밋 후 이벤트 발행
        //db 작업 완료 후 토큰 삭제 (일회성) -redis는 트랜잭션 밖에서 동작하므로 롤백이 안됨
        //-> 커밋 실패했는데 토큰 삭제 될 가능성
        //트랜잭션 커밋 후 삭제되도록 트랜잭션 밖으로 꺼내고 이벤트 발행
//        applicationEventPublisher.publishEvent(new ReservationCreatedEvent(request.getTotalPrice()));
    }

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleReservationCreated(ReservationCreatedEvent event){
//        priceTokenRedisService.delete(event.getPriceToken());
//    }

    @Recover
    public void recover(RuntimeException e,
                        ReservationRequest request, Long userId){
        log.error("예약 재시도 모두 실패 - reservationKey: {}",
                request.getReservationKey());

        if(e instanceof CustomException ce){
            throw ce;  // RESERVATION_UNAVAILABLE 그대로 던짐
        }
        throw new CustomException(ErrorCode.RESERVATION_CONFLICT);
    }
}
