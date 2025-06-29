package com.mysite.auth.domain.entity;

import com.mysite.auth.domain.enums.OAuthProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;  // 사용자 식별용

    private OAuthProvider provider;

    @Column(nullable = false)
    private String token;
}