package com.mysite.auth.service;

import com.mysite.auth.domain.entity.RefreshToken;
import com.mysite.auth.domain.entity.User;
import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.dto.response.TokenResponse;
import com.mysite.auth.exception.AuthException;
import com.mysite.auth.exception.GeneralException;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.repository.RefreshTokenRepository;
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
        User user = userService.findUserByEmailAndProvider(email, provider);

        RefreshToken savedToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(AuthException.REFRESH_TOKEN_NOT_FOUND));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new GeneralException(AuthException.REFRESH_TOKEN_MISMATCH);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return new TokenResponse(newAccessToken);
    }

}
