package com.hotel.reservation.controller;

import com.hotel.reservation.dto.AccessTokenResponse;
import com.hotel.reservation.dto.LoginRequest;
import com.hotel.reservation.dto.SignUpRequest;
import com.hotel.reservation.dto.TokenResponse;
import com.hotel.reservation.jwt.JwtProvider;
import com.hotel.reservation.service.AuthService;
import com.hotel.reservation.util.CookieUtil;
import jakarta.persistence.Access;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtProvider jwtProvider;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest request,
                                                     HttpServletResponse servletResponse){
        TokenResponse tokens =authService.login(request);

        //쿠키저장
        ResponseCookie rtc = cookieUtil.createRTCookie(tokens.getRefreshToken());
        servletResponse.addHeader("Set-Cookie", rtc.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AccessTokenResponse(tokens.getAccessToken()));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){
        String accessToken = jwtProvider.resolveToken(request);
        authService.logout(accessToken);

        ResponseCookie rtc = cookieUtil.deleteRTCookie();
        response.addHeader("Set-Cookie", rtc.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    //RTR방식 (at+rt재발급) - at만료 시, 새로고침으로 at가 null일 경우
    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(@CookieValue(name="refreshToken", required = false) String refreshToken,
                                          HttpServletResponse response){
        TokenResponse tokens = authService.reissue(refreshToken);

        ResponseCookie rtc = cookieUtil.createRTCookie(tokens.getRefreshToken());
        response.addHeader("Set-Cookie", rtc.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AccessTokenResponse(tokens.getAccessToken()));
    }

    @PostMapping("/signUp")
    ResponseEntity<AccessTokenResponse> signUp(@Valid SignUpRequest request,
                                               HttpServletResponse servletResponse){
        TokenResponse tokens = authService.saveUser(request);

        ResponseCookie rtc = cookieUtil.createRTCookie(tokens.getRefreshToken());
        servletResponse.addHeader("Set-Cookie", rtc.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AccessTokenResponse(tokens.getAccessToken()));
    }
}
