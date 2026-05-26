package com.hotel.order_server.repository;

import com.hotel.order_server.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,String> {
}
