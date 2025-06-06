package com.mysite.auth.domain.entity;

import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.domain.enums.UserRole;
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
@Table(name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email", "provider"}))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일: 로그인 ID 역할
    @Column(nullable = false)
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

}
