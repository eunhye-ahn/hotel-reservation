package com.hotel.payment.repository;

import com.hotel.payment.domain.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    //호텔 정산이력 조회
    Page<Settlement> findBySellerAccountOrderByCreatedAtDesc(String sellerAccount, Pageable pageable);
}
