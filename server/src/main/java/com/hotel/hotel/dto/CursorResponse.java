package com.hotel.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CursorResponse {
    private List<HotelResponse> content;
    private Long nextCursor;
    private boolean hasNext;

    public static CursorResponse of(List<HotelResponse> content, int size) {
        boolean hasNext = content.size() > size;

        List<HotelResponse> result = hasNext ? content.subList(0, size) : content;

        Long nextCursor = null;
        if (hasNext && !result.isEmpty()) {
            nextCursor = result.get(result.size() - 1).getHotelId();
        }

        return new CursorResponse(result, nextCursor, hasNext);
    }
}