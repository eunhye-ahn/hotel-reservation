package com.hotel.hotel.dto;

import com.hotel.hotel.domain.WishCollection;
import com.hotel.hotel.domain.WishList;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WishListCollectionResponse {
    private Long collectionId;
    private String name;
    private List<WishListResponse> items;
    private int count;

    public static WishListCollectionResponse from(WishCollection collection, List<WishList> wishList){
        List<WishListResponse> items = wishList.stream()
                .map(item->new WishListResponse(
                        item.getId(),
                        item.getHotel().getName(),
                        item.getHotel().getImageUrl(),
                        item.getHotel().getAddress()
                )).toList();

        return WishListCollectionResponse.builder()
                .collectionId(collection.getId())
                .name(collection.getName())
                .items(items)
                .count(items.size())
                .build();
    }
}
