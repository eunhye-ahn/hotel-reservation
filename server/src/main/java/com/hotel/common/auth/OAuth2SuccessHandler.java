package com.hotel.common.auth;

import com.hotel.common.util.CookieUtil;
import com.hotel.user.domain.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //인증된 principal에서 OAuth2User 꺼내기 => attribute
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        //attribute에서 userId, role 추출
        Long userId = (Long)oAuth2User.getAttribute("userId");
        String roleName = (String)oAuth2User.getAttribute("role");
        Role role = Role.valueOf(roleName);

        //jwt 발급
        String accessToken = jwtProvider.generateAccessToken(userId, role);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        ResponseCookie rtc = cookieUtil.createRTCookie(refreshToken);
        response.addHeader("Set-Cookie", rtc.toString());

        String frontendUrl = request.getHeader("Origin");
        String redirectUrl = frontendUrl+"/oauth2/redirect?accessToken="+accessToken;
        response.sendRedirect(redirectUrl);
    }
}
