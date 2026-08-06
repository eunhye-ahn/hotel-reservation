package com.hotel.hotel.dto;

import com.hotel.hotel.domain.WishList;

public record WishListResponse(
        Long wishListItemId,
        String hotelName,
        String hotelImageUrl,
        String hotelAddress,
        Long hotelId
) {

    public static WishListResponse from(WishList wishList){
        return new WishListResponse(
                wishList.getId(),
                wishList.getHotel().getName(),
                wishList.getHotel().getImageUrl(),
                wishList.getHotel().getAddress(),
                wishList.getHotel().getId()
        );
    }
}
