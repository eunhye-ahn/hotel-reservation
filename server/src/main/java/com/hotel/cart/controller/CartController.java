package com.hotel.cart.controller;

import com.hotel.cart.dto.CartItem;
import com.hotel.cart.dto.CartRequest;
import com.hotel.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    //장바구니 조회
    @GetMapping
    public ResponseEntity<List<CartItem>> getItems(@AuthenticationPrincipal Long userId){
        List<CartItem> result = cartService.getItems(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    //장바구니 추가
    @PostMapping
    public ResponseEntity<Void> addItem(@AuthenticationPrincipal Long userId, @RequestBody CartRequest request){
        CartItem item = CartItem.builder()
                .cartItemId(UUID.randomUUID().toString())
                .roomTypeId(request.getRoomTypeId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .createdAt(LocalDateTime.now())
                .build();
        cartService.addItem(userId, item);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //장바구니 제거
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Long userId, @PathVariable String cartItemId){
        cartService.removeItem(userId, cartItemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
