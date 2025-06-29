package com.mysite.auth.service;

import com.mysite.auth.domain.entity.RefreshToken;
import com.mysite.auth.domain.entity.User;
import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.dto.response.AuthResponse;
import com.mysite.auth.dto.response.TokenResponse;
import com.mysite.auth.exception.AuthException;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.repository.RefreshTokenRepository;
import com.mysite.auth.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponse reissueAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new GeneralException(AuthException.INVALID_REFRESH_TOKEN);
        }

        String email = jwtTokenProvider.getUserEmailFromToken(refreshToken);
        OAuthProvider provider = jwtTokenProvider.getProviderFromToken(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findByEmailAndProvider(email,
                        provider)
                .orElseThrow(() -> new GeneralException(AuthException.REFRESH_TOKEN_NOT_FOUND));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new GeneralException(AuthException.REFRESH_TOKEN_MISMATCH);
        }

        User user = userService.findUserByEmail(email);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return new TokenResponse(newAccessToken);
    }


    public AuthResponse login(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // 리프레시 토큰 DB 저장 (덮어쓰기 가능)
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(user.getEmail())
                        .provider(user.getProvider())
                        .token(refreshToken)
                        .build()
        );

        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getNickname());
    }
}
