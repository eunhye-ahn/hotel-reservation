package com.hotel.hotel.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HotelSearchResult {
    private List<Long> hotelIds;
    private String nextCursor;
    private boolean hasNext;
}
