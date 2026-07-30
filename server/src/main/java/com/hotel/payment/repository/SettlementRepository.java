package com.hotel.payment.repository;

import com.hotel.payment.domain.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, SettlementRepositoryCustom {
}
