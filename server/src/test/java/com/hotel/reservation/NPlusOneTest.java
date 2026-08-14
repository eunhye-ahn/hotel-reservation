package com.hotel.reservation;

import com.hotel.hotel.dto.RoomTypeInventoryParam;
import com.hotel.hotel.mapper.RoomTypeMapper;
import com.hotel.hotel.service.HotelService;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

@SpringBootTest
@Slf4j
public class NPlusOneTest {
    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private RoomTypeMapper roomTypeMapper;


    /**
     * public record RoomTypeInventoryParam (
     *         Long hotelId,
     *         LocalDate today,
     *         LocalDate startDate,
     *         LocalDate endDate,
     *         Integer totalDays,
     *         Integer numberOfRooms,
     *         Integer numberOfGuests
     * ){ }
     */

    @Test
    void 호텔_상세조회_쿼리횟수_측정(){
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        RoomTypeInventoryParam param
                = new RoomTypeInventoryParam(
                1L,
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                1,
                1,
                1
        );

        roomTypeMapper.findByRoomTypeFilter(param);

        long entityLoadCount = stats.getEntityLoadCount();       // 엔티티 개별 로드 수
        long prepareStmtCount = stats.getPrepareStatementCount(); // 실제 발생한 모든 JDBC PreparedStatement 수 (가장 정확)
        long queryCount = stats.getQueryExecutionCount();

        System.out.println("엔티티 로드 수: " + entityLoadCount);
        System.out.println("전체 PreparedStatement 수: " + prepareStmtCount);
        System.out.println("명시적 쿼리 수: " + queryCount);

        assertThat(prepareStmtCount).isLessThan(4);
    }
}
