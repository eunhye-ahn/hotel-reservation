package com.hotel.hotel.dto;

public record CheapestRateResult(
        int totalDemandRate,
        int totalMaxRate
) {
    public int calculateDiscountRate() {
        return (int) Math.round((double) (totalMaxRate - totalDemandRate) / totalMaxRate * 100);
    }
}