package com.hotel.hotel.dto;

import com.hotel.hotel.domain.WishCollection;
import com.hotel.hotel.domain.WishList;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


public record WishListCollectionResponse (
        Long collectionId,
        String name,
        List<WishListResponse> items,
        int count
){

    public static WishListCollectionResponse from(WishCollection collection, List<WishList> wishList){
        List<WishListResponse> items = wishList.stream()
                .map(WishListResponse::from).toList();

        return new WishListCollectionResponse(
                collection.getId(),
                collection.getName(),
                items,
                items.size()
        );
    }
}
