package com.hotel.hotel.dto;


import com.hotel.hotel.domain.WishCollection;
import com.hotel.hotel.domain.WishList;

public record AddWishListResponse (
        Long collectionId,
        String collectionName,
        String hotelImageUrl
){
    public static AddWishListResponse from(WishCollection collection, WishList wishList){

        return new  AddWishListResponse(
                collection.getId(),
                collection.getName(),
                wishList.getHotel().getImageUrl()
        );
    }
}
