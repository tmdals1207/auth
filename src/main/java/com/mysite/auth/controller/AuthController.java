package com.mysite.auth.controller;

import com.mysite.auth.dto.request.SignupRequest;
import com.mysite.auth.dto.response.ApiResponse;
import com.mysite.auth.dto.response.SignupResponse;
import com.mysite.auth.dto.response.TokenResponse;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.service.RefreshTokenService;
import com.mysite.auth.domain.User;
import com.mysite.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request) {
        // 1. 요청에서 refresh token 추출
        String refreshToken = jwtTokenProvider.resolveToken(request);

        // 2. 유효성 검사
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 리프레시 토큰입니다.");
        }

        // 3. 토큰에서 사용자 email 추출
        String email = jwtTokenProvider.getUserEmailFromToken(refreshToken);

        // 4. DB에 저장된 refresh token과 비교
        var savedToken = refreshTokenService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰 없음"));

        if (!savedToken.getToken().equals(refreshToken)) {
            return ResponseEntity.status(403).body("리프레시 토큰 불일치");
        }

        // 5. 사용자 정보로 새 Access Token 발급
        User user = userService.findUserByEmail(email);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        // 6. 클라이언트에 새 토큰 반환
        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ApiResponse<Void> errorResponse = new ApiResponse<>(401, "유효하지 않은 토큰입니다.", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String email = jwtTokenProvider.getUserEmailFromToken(token);

        // DB에서 리프레시 토큰 삭제
        refreshTokenService.deleteByEmail(email);

        ApiResponse<Void> successResponse = new ApiResponse<>(200, "로그아웃 완료", null);
        return ResponseEntity.ok(successResponse);
    }

}
