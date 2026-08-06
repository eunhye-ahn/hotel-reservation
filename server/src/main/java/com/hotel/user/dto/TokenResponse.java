package com.hotel.user.dto;

public record TokenResponse (
     String accessToken,
     String refreshToken
){}
