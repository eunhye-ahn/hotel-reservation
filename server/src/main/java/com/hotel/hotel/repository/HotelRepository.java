package com.hotel.hotel.repository;

import com.hotel.hotel.domain.Hotel;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;


public interface HotelRepository extends JpaRepository<Hotel,Long>, HotelRepositoryCustom {
    /**
     * JpaRepository 기본 제공 -> findAll(Pageable)
     * 하지만 Rate있는 호텔만 필터링해야함
     */
    @Query("select distinct r.hotel from Rate r where r.date = :date")
    Page<Hotel> findAllWithRate(@Param("date") LocalDate date, Pageable pageable);


    boolean existsByName(String name);

    boolean existsByAddress(String address);

    @Query("SELECT h FROM Hotel h WHERE h.lclsSystm2 = :lclsSystm2 AND h.lDongRegnCd = :lDongRegnCd AND h.id != :excludeId")
    List<Hotel> findSimilarTop30(
            @Param("lclsSystm2") String lclsSystm2,
            @Param("lDongRegnCd") String lDongRegnCd,
            @Param("excludeId") Long excludeId,
            Pageable pageable);
}
