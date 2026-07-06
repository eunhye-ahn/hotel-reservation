package com.hotel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateWishListResponse {
    private Long collectionId;
    private String collectionName;
}
