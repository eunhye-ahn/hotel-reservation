package com.hotel.reservation.jwt;

import com.hotel.reservation.domain.Role;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//토큰 생성/검증/파싱 + 유저정보꺼내는메서드
@Component
public class JwtProvider {
    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    //헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }
        return null;
    }

    //문자열 비밀키 -> jwt 서명용 키 객체로 변환
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    };

    public String generateAccessToken(Long userId, Role role){
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessExpiration))
                .signWith(getSigningKey())
                .compact(); //string으로 직렬화
    }

    public String generateRefreshToken(Long userId){
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    //jwt파싱 메서드
    private Claims parseClaim(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        if(token == null || token.isBlank()){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        try{
            parseClaim(token);
            return true;
        }catch(ExpiredJwtException e){
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }catch(JwtException e){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Long getUserIdFromToken(String token){
        Claims claims = parseClaim(token);
        return Long.parseLong(claims.getSubject());
    }

    //role은 string 타입이여도 권한설정에 이상 무!
    public String getUserRoleFromToken(String token){
        Claims claims = parseClaim(token);
        return claims.get("role", String.class);
    }

    public Long getRefreshExpiration(){return refreshExpiration;}

    /**
     * -로그아웃
     * -> 클라이언트가 AT를 삭제했음에도 불구하고 악의적인 사용자가 AT를 복사해둔 경우
     * -> 서버는 서명 + 만료시간만 검증
     * -> 그냥 통과
     *
     * [Redis 블랙리스트]
     * -로그아웃
     * -> 복사한 AT로 API 호출
     * -> 서명검증+만료시간확인
     * -> 블랙리스트확인 ---------여기서 막힘
     * -> 401반환
     *
     * [WHY]
     * 블랙리스트에 만료된 토큰은 삭제하기위해
     * at와 남은시간 함께 저장
     *
     * @param accessToken
     * @return
     */
    public Long getRemainingExpiration(String accessToken){
        Claims claims = parseClaim(accessToken);
        return claims.getExpiration().getTime()-System.currentTimeMillis();
    }
}
