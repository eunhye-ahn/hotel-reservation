package com.hotel.hotel.controller;

import com.hotel.hotel.dto.WishListCollectionResponse;
import com.hotel.hotel.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wish")
public class WishController {
    private final WishService wishService;

    //위시리스트 그룹 추가
    @PostMapping("/collection")
    public ResponseEntity<Void> createCollection(@AuthenticationPrincipal Long userId, @RequestBody String collectionName){
        wishService.createCollection(userId, collectionName);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    //위시리스트 그룹 조회
    @GetMapping("/collection")
    public ResponseEntity<List<WishListCollectionResponse>> getCollections(@AuthenticationPrincipal Long userId){
        List<WishListCollectionResponse> result = wishService.getCollections(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

}
