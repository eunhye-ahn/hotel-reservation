package com.hotel.hotel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotelSearchRequest {
    private String q;
}
