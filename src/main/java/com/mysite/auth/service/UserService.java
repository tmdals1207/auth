package com.mysite.auth.service;

import com.mysite.auth.domain.User;
import com.mysite.auth.eNum.OAuthProvider;
import com.mysite.auth.eNum.UserRole;
import com.mysite.auth.exception.UserNotFoundException;
import com.mysite.auth.repository.UserRepository;
import com.mysite.auth.dto.request.SignupRequest;
import com.mysite.auth.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .provider(OAuthProvider.LOCAL)
                .providerId(null)
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);

        return new SignupResponse(user.getId(), "회원가입이 완료되었습니다.");
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

}
