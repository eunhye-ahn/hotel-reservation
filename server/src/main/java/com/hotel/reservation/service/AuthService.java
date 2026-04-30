package com.hotel.reservation.service;

import com.hotel.reservation.domain.User;
import com.hotel.reservation.dto.LoginRequest;
import com.hotel.reservation.dto.SignUpRequest;
import com.hotel.reservation.dto.TokenResponse;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.jwt.JwtProvider;
import com.hotel.reservation.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisService redisService;

    //로그인
    public TokenResponse login(LoginRequest request){
        //이메일 검사
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        //토큰발급
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        //순환참조방지 - authservice에서 만료시간 넘겨줌
        Long expiration = jwtProvider.getRefreshExpiration();

        //rt-redis저장 (scale-out 고려)
        redisService.saveRefreshToken(user.getId(),refreshToken,expiration);

        return new TokenResponse(accessToken, refreshToken);
    }

    //로그아웃
    public void logout(String accesstoken){
        Long expiration = jwtProvider.getRemainingExpiration(accesstoken);

        //redis 블랙리스트에 at저장
        redisService.saveBlackList(accesstoken, expiration);

        Long userId = jwtProvider.getUserIdFromToken(accesstoken);

        //redis에 rt삭제
        redisService.deleteRefreshToken(userId);
    }

    /**
     * at가 토큰검증했는데 만료된경우 => 401반환
     * 새로고침해서 at가 null이 됐을때
     *
     */
    //at+rt재발급 - rtr방식
    public TokenResponse reissue(String refreshToken){
        //토큰검증
        jwtProvider.validateToken(refreshToken);

        //유저아이디 추출
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        //rt유효성검사
        String savedRT = redisService.getRefreshToken(userId);
        if(savedRT == null || !savedRT.equals(refreshToken)){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        //기존 rt 삭제
        redisService.deleteRefreshToken(userId);

        //유저조회
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        //at 재발급
        String newAT = jwtProvider.generateAccessToken(userId, user.getRole());

        //새 rt 발급
        String newRT = jwtProvider.generateRefreshToken(userId);

        //redis에 rt저장
        redisService.saveRefreshToken(userId, newRT, jwtProvider.getRefreshExpiration());

        return new TokenResponse(newAT, newRT);
    }

    //회원가입
    @Transactional
    public TokenResponse saveUser(SignUpRequest request){
        //이메일 유효성검사
        if(userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        //비밀번호 유효성검사 -> valid

        //저장
        userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .build());

        //자동로그인
        return login(new LoginRequest(request.getEmail(), request.getPassword()));
    }
}
