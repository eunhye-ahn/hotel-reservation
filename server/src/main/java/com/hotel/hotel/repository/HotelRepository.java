package com.hotel.hotel.repository;

import com.hotel.hotel.domain.Hotel;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;


public interface HotelRepository extends JpaRepository<Hotel,Long> {


    @Query("SELECT h " +
            "FROM Hotel h " +
            "LEFT JOIN WishList w ON w.hotel = h " +
            "GROUP BY h " +
            "ORDER BY COUNT(w) DESC")
    List<Hotel> findPopularByWishCount(Pageable pageable);

    boolean existsByName(String name);

    boolean existsByAddress(String address);

    @Query("SELECT h FROM Hotel h WHERE h.lclsSystm2 = :lclsSystm2 AND h.lDongRegnCd = :lDongRegnCd AND h.id != :excludeId")
    List<Hotel> findSimilarTop15(
            @Param("lclsSystm2") String lclsSystm2,
            @Param("lDongRegnCd") String lDongRegnCd,
            @Param("excludeId") Long excludeId,
            Pageable pageable);
}
