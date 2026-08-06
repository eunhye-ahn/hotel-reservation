package com.hotel.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest (
    @NotBlank(message = "이름을 입력하세요")
     String name,

    @Email(message = "이메일 형식이 아닙니다")
    @NotBlank(message = "이메일을 입력하세요")
     String email,

    @NotBlank(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^010\\d{8}$",
            message = "전화번호 형식이 올바르지 않습니다")
     String phone,

    @NotBlank(message = "비밀번호를 입력하세요")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message="비밀번호 8자 이상, 영문+숫자 조합이어야 합니다")
     String password
){}
