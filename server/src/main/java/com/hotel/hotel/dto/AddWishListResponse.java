package com.hotel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddWishListResponse {
    private String collectionName;
    private String hotelImageUrl;
}
