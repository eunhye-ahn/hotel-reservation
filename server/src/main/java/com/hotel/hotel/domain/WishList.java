package com.hotel.hotel.domain;

import com.hotel.reservation.domain.BaseTime;
import com.hotel.reservation.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "wish_list", uniqueConstraints = @UniqueConstraint(columnNames = {"collection_id", "hotel_id"}))
public class WishList extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="collection_id")
    private WishCollection wishCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

    public WishList(WishCollection wishCollection, Hotel hotel){
        this.wishCollection = wishCollection;
        this.hotel = hotel;
    }

    public void update(WishList wishList,WishCollection wishCollection){
        this.wishCollection = wishCollection;
    }
}
