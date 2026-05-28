package com.hotel.hotel_server.repository;

import com.hotel.hotel_server.domain.Hotel;
import org.springframework.data.repository.query.Param;

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


    boolean existsByName(String name);

    boolean existsByAddress(String address);
}
