package com.hotel.order_server.service;

import com.hotel.order_server.domain.Order;
import com.hotel.order_server.domain.OrderStatus;
import com.hotel.order_server.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    //주문생성
    public String createOrder(Long userId){
        String orderId = UUID.randomUUID().toString();

        Order order = Order.builder()
                .orderId(orderId)
                .userId(userId)
                .orderStatus(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        log.info("order created - orderId : {}", orderId);

        return orderId;
    }
}
