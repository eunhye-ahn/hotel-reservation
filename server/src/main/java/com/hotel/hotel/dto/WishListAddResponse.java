package com.hotel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishListAddResponse {
    private Long collectionId;
    private String collectionName;
    private String hotelImageUrl;
}
