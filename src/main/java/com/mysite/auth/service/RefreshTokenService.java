package com.mysite.auth.service;

import com.mysite.auth.domain.entity.RefreshToken;
import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(String email, OAuthProvider provider, String refreshToken) {
        refreshTokenRepository.findByEmailAndProvider(email, provider).ifPresentOrElse(
                token -> token = refreshTokenRepository.save(RefreshToken.builder()
                        .id(token.getId())
                        .email(email)
                        .provider(provider)
                        .token(refreshToken)
                        .build()),
                () -> refreshTokenRepository.save(
                        RefreshToken.builder().email(email).provider(provider).token(refreshToken)
                                .build()
                )
        );
    }

    public Optional<RefreshToken> findByEmail(String email) {
        return refreshTokenRepository.findByEmail(email);
    }

    public void deleteByEmail(String email) {
        refreshTokenRepository.findByEmail(email).ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> findByEmailAndProvider(String email, OAuthProvider provider) {
        return refreshTokenRepository.findByEmailAndProvider(email, provider);
    }
}
