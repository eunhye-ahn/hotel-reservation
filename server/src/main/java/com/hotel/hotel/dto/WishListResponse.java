package com.hotel.hotel.dto;

import com.hotel.hotel.domain.WishList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishListResponse {
    private Long wishListItemId;
    private String hotelImageUrl;

    public static WishListResponse from(WishList wishList){
        return new WishListResponse(wishList.getId(), wishList.getHotel().getImageUrl());
    }
}
