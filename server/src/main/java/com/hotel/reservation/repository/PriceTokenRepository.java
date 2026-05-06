package com.hotel.reservation.repository;

import com.hotel.reservation.domain.PriceToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceTokenRepository extends JpaRepository<PriceToken, String> {
}
