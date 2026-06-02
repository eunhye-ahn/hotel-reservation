package com.hotel.hotel_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheapestRateResult {
    private int totalDemandRate;
    private int totalMaxRate;

    public int calculateDiscountRate(){
        return (int) Math.round((double)(totalMaxRate - totalDemandRate) / totalMaxRate * 100);
    }
}
