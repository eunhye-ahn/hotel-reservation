package com.hotel.hotel.repository;


import com.hotel.hotel.domain.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList,Long> {
    List<WishList> findByWishCollectionId(Long collectionId);

}
