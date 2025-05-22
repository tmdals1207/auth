package com.mysite.auth.jwt;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String email;  // 사용자 식별용

    @Column(nullable = false)
    private String token;
}