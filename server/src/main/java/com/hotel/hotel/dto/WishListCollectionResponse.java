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

    public static WishListCollectionResponse from(WishCollection collection, List<WishList> wishList){
        return WishListCollectionResponse.builder()
                .collectionId(collection.getId())
                .name(collection.getName())
                .items((wishList.stream()
                        .map((item)->
                                new WishListResponse(item.getId(), item.getHotel().getImageUrl()))).toList())
                .build();
    }
}
