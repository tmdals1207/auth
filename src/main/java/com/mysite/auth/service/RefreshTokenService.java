package com.mysite.auth.service;

import com.mysite.auth.domain.entity.RefreshToken;
import com.mysite.auth.domain.entity.User;
import com.mysite.auth.repository.RefreshTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(User user, String refreshToken) {
        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                token -> refreshTokenRepository.save(token.toBuilder().token(refreshToken).build()),
                () -> refreshTokenRepository.save(
                        RefreshToken.builder()
                                .user(user)
                                .token(refreshToken)
                                .build()
                )
        );

    }

    public void deleteByUser(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> findByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }
}
