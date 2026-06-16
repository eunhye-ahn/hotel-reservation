package com.hotel.cart.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.cart.dto.CartItem;
import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "cart:member:";
    private static final long TTL_DAYS = 30;
    private static final int MAX_ITEMS = 20;

    private String key(Long userId){
        return KEY_PREFIX+userId;
    }

    //장바구니 전체조회
    public List<CartItem> getItems(Long userId){
        List<Object> raw = redisTemplate.opsForList().range(key(userId),0,-1);
        if(raw == null) return List.of();
        return raw.stream()
                .map(o->objectMapper.convertValue(o,CartItem.class))
                .toList();
    }

    //장바구니 추가(20개 초과 시 예외)
    public void addItem(Long userId, CartItem item){
        String key = key(userId);
        Long size = redisTemplate.opsForList().size(key);
        if(size != null && size >= MAX_ITEMS){
            throw new CustomException(ErrorCode.CART_FULL);
        }
        redisTemplate.opsForList().rightPush(key,item);
        redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
    }

    //장바구니 삭제
    public void removeItem(Long userId, String cartItemId){
        String key = key(userId);

        List<CartItem> items = getItems(userId).stream()
                .filter(item -> !item.getCartItemId().equals(cartItemId))
                .toList();

        redisTemplate.delete(key);
        if(!items.isEmpty()) {
            items.forEach(item -> redisTemplate.opsForList().rightPush(key, item));
            redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
        }
    }

}
