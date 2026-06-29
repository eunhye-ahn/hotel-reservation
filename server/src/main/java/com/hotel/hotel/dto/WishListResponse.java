package com.hotel.hotel.dto;

import com.hotel.hotel.domain.WishList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishListResponse {
    private Long wishListItemId;
    private String hotelName;
    private String hotelImageUrl;
    private String hotelAddress;

    public static WishListResponse from(WishList wishList){
        return new WishListResponse(wishList.getId(), wishList.getHotel().getName(), wishList.getHotel().getImageUrl(), wishList.getHotel().getAddress());
    }
}
