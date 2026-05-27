package com.hotel.order_server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "orders")
@Entity
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class Order {
    @Id
    private String orderId;

    @Column(nullable = false)
    private Long userId;
}
