package com.hotel.reservation.dto;

import com.hotel.reservation.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private String name;
    private String email;
    private String phone;

    public static UserInfoResponse from(User user){
        return UserInfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
