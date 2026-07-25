package com.hotel.admin.service;

import com.hotel.admin.dto.*;
import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Room;
import com.hotel.hotel.domain.RoomTypeInventory;
import com.hotel.hotel.repository.RoomRepository;
import com.hotel.hotel.repository.RoomTypeInventoryRepository;
import com.hotel.payment.domain.*;
import com.hotel.payment.service.PaymentService;
import com.hotel.reservation.domain.ReservationSearchType;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.domain.ReservationStatus;
import com.hotel.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final PaymentService paymentService;
    private final RoomTypeInventoryRepository roomTypeInventoryRepository;
    public Page<AdminReservationSearchResponse> getReservations(LocalDate startDate, LocalDate endDate, ReservationSearchType searchType, String keyword, ReservationStatus status,Boolean roomAssigned, Pageable pageable) {

        Page<Reservation> result = reservationRepository.searchByReservation(startDate, endDate, searchType, keyword, status, roomAssigned,  pageable);

        return result.map(AdminReservationSearchResponse::from);
    }

    public AdminReservationDetailResponse getReservationDetail(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return AdminReservationDetailResponse.from(reservation);
    }

    //객실조회
    public List<AdminRoomResponse> getRoomsByReservation(Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        List<Room> rooms = roomRepository.findAllByRoomType(reservation.getRoomType());
        Long currentlyRoomId = reservation.getRoom() != null ? reservation.getRoom().getId() : null;

        return rooms.stream()
                .map(room -> AdminRoomResponse.builder()
                        .id(room.getId())
                        .roomTypeName(room.getRoomType().getName())
                        .roomName(room.getName())
                        .roomNumber(room.getNumber())
                        .floor(room.getFloor())
                        .roomStatus(room.isUsable())
                        .available(isRoomAvailable(reservation, room))
                        .currentlyAssigned(room.getId().equals(currentlyRoomId))
                        .build())
                .toList();
    }

    @Transactional
    public void assignRoom(Long reservationId, Long roomId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        Room room = roomRepository.findById(roomId).orElseThrow(()-> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        //검증
        validateSameRoomType(reservation, room);
        validateRoomUsable(room);
        validateNoOverlap(reservation, room);

        reservation.assignRoom(room);
    }

    //배정취소
    @Transactional
    public void unassignRoom(Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.unassignRoom();
    }

    //예약취소 - 관리자
    @Transactional
    public void cancelReservationByAdmin(Long reservationId, String reason){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        //예약상태변경 검증 룸초기화
        reservation.cancelByAdmin(reason);

        //재고복구(중복코드)
        List<RoomTypeInventory> inventories = roomTypeInventoryRepository
                .findByRoomTypeIdAndDateBetween(reservation.getRoomType().getId(), reservation.getStartDate(), reservation.getEndDate().minusDays(1));

        inventories.forEach(i -> i.restore(reservation.getNumberOfRooms()));

        paymentService.cancel(reservationId, reason);
        reservation.refund();
        //정산
        paymentService.reverseSettlement(reservation);
    }

    private void validateSameRoomType(Reservation reservation, Room room){
        if(!reservation.getRoomType().getId().equals(room.getRoomType().getId())){
            throw new CustomException(ErrorCode.ROOM_TYPE_MISMATCH);
        }
    }

    private void validateNoOverlap(Reservation reservation, Room room){
        boolean exists = isOverlapping(reservation, room);
        if(exists){
            throw new CustomException(ErrorCode.ROOM_ALREADY_OCCUPIED);
        }
    }

    private void validateRoomUsable(Room room){
        if(!isUsable(room)){
            throw new CustomException(ErrorCode.ROOM_NOT_USABLE);
        }
    }

    private boolean isUsable(Room room){
        return room.isUsable();
    }

    private boolean isOverlapping(Reservation reservation, Room room){
        return reservationRepository.existsOverlappingReservation(room.getId(), reservation.getStartDate(), reservation.getEndDate(), reservation.getId());
    }

    private boolean isRoomAvailable(Reservation reservation, Room room){
        return isUsable(room) && !isOverlapping(reservation, room);
    }

}
