package com.hotel.hotel.domain;

import com.hotel.common.domain.BaseTime;
import com.hotel.reservation.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "wish_collection")
@Entity
@Getter
@NoArgsConstructor
public class WishCollection extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    public WishCollection(User user, String name){
        this.user = user;
        this.name = name;
    }
}
