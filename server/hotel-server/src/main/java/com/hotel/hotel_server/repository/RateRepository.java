package com.hotel.hotel_server.repository;

import com.hotel.hotel_server.domain.Rate;
import com.hotel.hotel_server.domain.RoomType;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * demandRate가 가장 낮은 roomType의 maxRate, demandRate 같이 가져오기
 * @Param 값을 :에 바인딩 => sql의 ? 대신 이름으로 바인딩하는 방법 => 가독성
 */
public interface RateRepository extends JpaRepository<Rate,Long> {
    @Query("""
        select r
        from Rate r
        where r.roomType.hotel.id = :hotelId
        and r.date = :date
        order by r.demandRate asc
        limit 1
        """)
    Optional<Rate> findCheapestRate(@Param("hotelId") Long hotelId, @Param("date") LocalDate date);

    //이거 나중에 삭제하도록 코드 구조 변경
    Optional<Rate> findByRoomTypeAndDate(RoomType roomType, LocalDate date);

    void deleteByRoomType(RoomType roomType);

    List<Rate> findByRoomTypeIdAndDateBetween(Long roomTypeId, LocalDate startDate, LocalDate endDate);
}
