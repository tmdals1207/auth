package com.mysite.auth.service;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.dto.response.AuthResponse;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.domain.entity.RefreshToken;
import com.mysite.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse login(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 리프레시 토큰 DB 저장 (덮어쓰기 가능)
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(user.getEmail())
                        .token(refreshToken)
                        .build()
        );

        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getNickname());
    }
}