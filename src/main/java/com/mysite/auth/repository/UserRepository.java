package com.mysite.auth.repository;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.domain.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndProvider(String email, OAuthProvider provider);
}
