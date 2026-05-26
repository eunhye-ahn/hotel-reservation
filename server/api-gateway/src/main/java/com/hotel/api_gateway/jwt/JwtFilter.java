package com.hotel.api_gateway.jwt;
import com.hotel.api_gateway.exception.CustomException;
import com.hotel.api_gateway.exception.ErrorCode;
import com.hotel.api_gateway.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

                //헤더에 userId 추가 -> 각 서버로 전달
                log.info("userId: {}", userId);  // 추가
                request.setAttribute("X-User-Id", String.valueOf(userId));
                log.info("attribute set: {}", request.getAttribute("X-User-Id"));  // 추가

                //원본 요청에 오버라이드
                HttpServletRequestWrapper wrapper =
                        new HttpServletRequestWrapper(request) {
                            @Override
                            public String getHeader(String name) {
                                if ("X-User-Id".equals(name)) return String.valueOf(userId);
                                return super.getHeader(name);
                            }

                            @Override
                            public java.util.Enumeration<String> getHeaderNames() {
                                java.util.List<String> names = java.util.Collections.list(super.getHeaderNames());
                                names.add("X-User-Id");
                                return java.util.Collections.enumeration(names);
                            }

                            @Override
                            public java.util.Enumeration<String> getHeaders(String name) {
                                if ("X-User-Id".equals(name)) {
                                    return java.util.Collections.enumeration(
                                            java.util.List.of(String.valueOf(userId))
                                    );
                                }
                                return super.getHeaders(name);
                            }
                        };

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(wrapper, response);
                return;
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
