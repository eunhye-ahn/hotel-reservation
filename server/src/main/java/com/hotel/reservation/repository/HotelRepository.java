package com.hotel.reservation.repository;

import com.hotel.reservation.domain.Hotel;
import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;


public interface HotelRepository extends JpaRepository<Hotel,Long> {
    /**
     * JpaRepository 기본 제공 -> findAll(Pageable)
     * 하지만 Rate있는 호텔만 필터링해야함
     */
    @Query("select distinct r.hotel from Rate r where r.date = :date")
    Page<Hotel> findAllWithRate(@Param("date") LocalDate date, Pageable pageable);
}
