package com.hotel.hotel.controller;

import com.hotel.hotel.dto.*;
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
    public ResponseEntity<Void> createCollection(@AuthenticationPrincipal Long userId, @RequestBody WishCollectionsRequest request){
        wishService.createCollection(userId, request.getCollectionName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    //위시 그룹 전체 조회
    @GetMapping("/collection/all")
    public ResponseEntity<List<WishListCollectionResponse>> getCollections(@AuthenticationPrincipal Long userId){
        List<WishListCollectionResponse> result = wishService.getCollections(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //위시 그룹 상세조회
    @GetMapping("/collection")
    public ResponseEntity<WishListCollectionResponse> getCollection(@AuthenticationPrincipal Long userId, @RequestParam Long collectionId){
        WishListCollectionResponse result = wishService.getCollection(userId, collectionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //위시리스트 아이템 추가
    @PostMapping("/list")
    public ResponseEntity<AddWishListResponse> addWishList(@AuthenticationPrincipal Long userId, @RequestBody AddWishListRequest request) {
        AddWishListResponse result = wishService.addWishList(userId, request.getHotelId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    //리스트 그룹 이동
    @PostMapping("/move")
    public ResponseEntity<MoveWishResponse> moveCollection(@RequestBody MoveWishRequest request){
        MoveWishResponse result = wishService.moveCollection(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //위시리스트 취소
    @DeleteMapping("/cancle")
    public ResponseEntity<Void> cancelWishList(@RequestParam Long wishListId){
        wishService.cancelWishList(wishListId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
