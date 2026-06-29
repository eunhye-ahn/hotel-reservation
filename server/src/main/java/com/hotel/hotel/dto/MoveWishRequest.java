package com.hotel.hotel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoveWishRequest {
    private Long collectionId;
    private Long listId;
}
