package com.mysite.auth.domain;

import com.mysite.auth.eNum.OAuthProvider;
import com.mysite.auth.eNum.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일: 로그인 ID 역할
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileImage;

    // OAuth 제공자: "google", "kakao", "naver", "local"
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider provider;

    // OAuth 제공자에서 받은 고유 ID (local은 null)
    private String providerId;

    // 권한: ROLE_USER, ROLE_ADMIN 등
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // 생성/수정일 자동 기록
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 상태 플래그 (선택): 탈퇴, 비활성 등 처리 가능
    private boolean isActive = true;
}
