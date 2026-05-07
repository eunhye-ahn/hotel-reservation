package com.hotel.reservation.controller;

import com.hotel.reservation.dto.AccessTokenResponse;
import com.hotel.reservation.dto.SignUpRequest;
import com.hotel.reservation.dto.UserInfoResponse;
import com.hotel.reservation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //내 정보 조회
    @GetMapping("/myInfo")
    ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal Long userId){
        UserInfoResponse result = userService.getMyInfo(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
