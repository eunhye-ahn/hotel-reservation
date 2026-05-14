package com.hotel.reservation.jwt;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * [WHAT] 컨트롤러 진입 필터 로직
 * [흐름]
 *      *  1. 헤더에서 토큰 추출
 *      *  2. /auth/reissue 요청이면 검증 없이 통과 (RT 재발급 엔드포인트)
 *      *  3. 토큰이 있으면 -> 검증 -> securitycontext 인증 정보 저장
 *      *  4. 토큰 없으면 -> 저장 안함 -> security가 비인증 요청으로 처리
 *      *  5. 다음 필터로 넘기거나 컨트롤러 진입
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtProvider.resolveToken(request);

            if (request.getRequestURI().equals("/api/auth/reissue")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (token != null) {
                //블랙리스트검사
                if(redisService.isBlackList(token)){
                    throw new CustomException(ErrorCode.LOGOUT_TOKEN);
                }
                //토큰검증
                jwtProvider.validateToken(token);

                //시큐리티 인증객체로 저장
                Long userId = jwtProvider.getUserIdFromToken(token);
                String role = jwtProvider.getUserRoleFromToken(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch(CustomException e){
            SecurityContextHolder.clearContext();
            //provider에서 던진 커스텀익셉션 처리
            response.setStatus(e.getErrorCode().getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"code\":\"" + e.getErrorCode().name() + "\"," +
                            "\"message\":\"" + e.getErrorCode().getMessage() + "\"}"
            );
            return;
        }catch(Exception e){
            SecurityContextHolder.clearContext();
            //그외 인증 예외처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
