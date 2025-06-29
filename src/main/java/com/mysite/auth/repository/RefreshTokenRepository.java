package com.mysite.auth.repository;

import com.mysite.auth.domain.entity.RefreshToken;
import com.mysite.auth.domain.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByEmail(String email);

    Optional<RefreshToken> findByEmailAndProvider(String email, OAuthProvider provider);
}
