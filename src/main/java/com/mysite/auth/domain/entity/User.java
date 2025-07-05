package com.mysite.auth.domain.entity;

import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "provider"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // 이메일: 로그인 ID 역할

    private String password; // 일반 로그인 사용자만 사용

    @Column(nullable = false)
    private String nickname;

    private String profileImage;

    // OAuth 제공자: "google", "kakao", "naver", "local"
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider provider; // OAuth 사용자만 사용

    // OAuth 제공자에서 받은 고유 ID (local은 null)
    private String providerId; // OAuth 사용자만 사용

    // 권한: ROLE_USER, ROLE_ADMIN 등
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // 생성/수정일 자동 기록
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static User ofLocalUser(String email, String password, String nickname, UserRole role) {

        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(OAuthProvider.LOCAL)
                .role(role)
                .build();
    }

    public static User ofOAuthUser(String email, String nickname, String profileImage,
            String providerId, OAuthProvider provider, UserRole role) {

        return User.builder()
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .providerId(providerId)
                .provider(provider)
                .role(role)
                .build();
    }


}
