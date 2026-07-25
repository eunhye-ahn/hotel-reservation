package com.hotel.admin.service;

import com.hotel.admin.dto.AdminReservationDetailResponse;
import com.hotel.admin.dto.AdminReservationSearchResponse;
import com.hotel.admin.dto.AdminRoomListResponse;
import com.hotel.admin.dto.AssignmentRoomRequest;
import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Room;
import com.hotel.hotel.repository.RoomRepository;
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

    public Page<AdminReservationSearchResponse> getReservations(LocalDate startDate, LocalDate endDate, ReservationSearchType searchType, String keyword, ReservationStatus status,Boolean roomAssigned, Pageable pageable) {

        Page<Reservation> result = reservationRepository.searchByReservation(startDate, endDate, searchType, keyword, status, roomAssigned,  pageable);

        return result.map(AdminReservationSearchResponse::from);
    }

    public AdminReservationDetailResponse getReservationDetail(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return AdminReservationDetailResponse.from(reservation);
    }

    //방조회
    public List<AdminRoomListResponse> getRoomsByReservation(Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        List<Room> rooms = roomRepository.findAllByRoomType(reservation.getRoomType());

        return rooms.stream()
                .map(room -> AdminRoomListResponse.builder()
                        .id(room.getId())
                        .roomTypeName(room.getRoomType().getName())
                        .roomName(room.getName())
                        .roomNumber(room.getNumber())
                        .floor(room.getFloor())
                        .roomStatus(room.isUsable())
                        .available(isRoomAvailable(reservation, room))
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
