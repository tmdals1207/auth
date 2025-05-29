package com.mysite.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.auth.domain.entity.User;
import com.mysite.auth.dto.request.LoginRequest;
import com.mysite.auth.dto.request.SignupRequest;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.repository.UserRepository;
import com.mysite.auth.service.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RefreshTokenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("회원가입 → 로그인 → 리프레시 토큰 저장 → 만료된 엑세스 토큰으로 재발급")
    void signupLoginAndReissueAccessTokenTest() throws Exception {
        // 1. 회원가입
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("refreshuser@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setNickname("리프레시유저");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원가입 성공"));

        // 2. 로그인
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("refreshuser@example.com");
        loginRequest.setPassword("password123");

        String accessToken = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(cookie().exists("accessToken"))
                .andReturn()
                .getResponse()
                .getCookie("accessToken")
                .getValue();

        // 3. 리프레시 토큰 발급/저장 (직접 서비스 계층 사용)
        User user = userRepository.findByEmail("refreshuser@example.com").orElseThrow();
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        refreshTokenService.save(user.getEmail(), refreshToken);

        // 4. 만료된 액세스 토큰(혹은 그냥 임의 값) + 저장된 리프레시 토큰으로 재발급 요청
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer expired.token.value")
                        .header("Refresh-Token", refreshToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.message").value("토큰 재발급 성공"));

        // 5. DB에 사용자 남아있는지 검증(Optional)
        assertThat(userRepository.findByEmail("refreshuser@example.com")).isPresent();
    }
}