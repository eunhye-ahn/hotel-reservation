package com.hotel.hotel.controller;

import com.hotel.hotel.domain.WishCollection;
import com.hotel.hotel.dto.*;
import com.hotel.hotel.service.WishService;
import com.hotel.reservation.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wish")
public class WishController {
    private final WishService wishService;
    private final CookieUtil cookieUtil;

    //위시리스트 그룹 추가
    @PostMapping("/collection")
    public ResponseEntity<Void> createCollection(@AuthenticationPrincipal Long userId, @RequestBody WishCollectionsRequest request){
        WishCollection result = wishService.createCollection(userId, request.getCollectionName());

        //쿠키저장
        ResponseCookie cookie = cookieUtil.createWishCollectionCookie(result.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
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
    public ResponseEntity<AddWishListResponse> addWishList(@AuthenticationPrincipal Long userId,
                                                           @RequestParam Long hotelId,
                                                           @CookieValue(value = "wish-collection", required = false) Long collectionId) {
        AddWishListResponse result = wishService.addWishList(userId, hotelId, collectionId);

        //쿠키저장 (이전 쿠키가 로그아웃 또는 자동로그아웃되고도 남아있어서 에러 발생) => 수정필요
        ResponseCookie cookie = cookieUtil.createWishCollectionCookie(result.getCollectionId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
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

    //위시리스트 취소 => wishListId => hotelId로 변경 필요
    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelWishList(@AuthenticationPrincipal Long userId, @RequestParam Long hotelId){
        wishService.cancelWishList(userId, hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //위시여부확인
    @GetMapping("/check")
    public ResponseEntity<Boolean> getWishedChecked(@AuthenticationPrincipal Long userId, @RequestParam Long hotelId){
        boolean wCheck = wishService.getWishedStatus(userId, hotelId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(wCheck);
    }
}
