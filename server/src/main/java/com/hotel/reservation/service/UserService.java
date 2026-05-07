package com.hotel.reservation.service;

import com.hotel.reservation.domain.User;
import com.hotel.reservation.dto.UserInfoResponse;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //내 정보조회
    public UserInfoResponse getMyInfo(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.from(user);
    }
}
