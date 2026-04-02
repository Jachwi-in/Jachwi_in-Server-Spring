package com.capstone.Jachwi_inServerSpring.service;

import com.capstone.Jachwi_inServerSpring.domain.User;
import com.capstone.Jachwi_inServerSpring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Main Server는 토큰에서 꺼낸 email로 유저 조회만 담당
    // 로그인/회원가입은 Auth Server(8081)가 담당
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
