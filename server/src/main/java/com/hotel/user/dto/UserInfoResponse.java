package com.hotel.user.dto;

import com.hotel.user.domain.User;

public record UserInfoResponse (
     String name,
     String email,
     String phone
){
    public static UserInfoResponse from(User user){
        return new UserInfoResponse(
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
