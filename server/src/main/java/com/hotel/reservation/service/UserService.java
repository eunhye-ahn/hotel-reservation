package com.hotel.reservation.service;

import com.hotel.reservation.domain.User;
import com.hotel.reservation.dto.SignUpRequest;
import com.hotel.reservation.dto.TokenResponse;
import com.hotel.reservation.exception.CustomException;
import com.hotel.reservation.exception.ErrorCode;
import com.hotel.reservation.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


}
