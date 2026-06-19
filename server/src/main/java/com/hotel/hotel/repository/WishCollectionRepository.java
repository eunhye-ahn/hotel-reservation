package com.hotel.hotel.repository;

import com.hotel.hotel.domain.WishCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishCollectionRepository extends JpaRepository<WishCollection, Long> {
    boolean existsByUserIdAndName(Long userId, String name);
    boolean existsByUserId(Long userId);
    Optional<WishCollection> findTop1ByUserIdOrderByCreatedDesc(Long userId);
    List<WishCollection> findByUserIdOrderByCreatedDesc(Long userId);
}
