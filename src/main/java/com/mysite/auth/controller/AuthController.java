package com.mysite.auth.controller;

import com.mysite.auth.dto.request.LoginRequest;
import com.mysite.auth.dto.request.SignupRequest;
import com.mysite.auth.dto.response.ApiResponse;
import com.mysite.auth.dto.response.LoginResponse;
import com.mysite.auth.dto.response.SignupResponse;
import com.mysite.auth.dto.response.TokenResponse;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.service.RefreshTokenService;
import com.mysite.auth.domain.entity.User;
import com.mysite.auth.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<ApiResponse<TokenResponse>> reissueAccessToken(HttpServletRequest request) {
        // 1. 요청에서 refresh token 추출
        String refreshToken = jwtTokenProvider.resolveToken(request);

        // 2. 유효성 검사
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(401, "유효하지 않은 리프레시 토큰입니다.", null));
        }

        // 3. 토큰에서 사용자 email 추출
        String email = jwtTokenProvider.getUserEmailFromToken(refreshToken);

        // 4. DB에 저장된 refresh token과 비교
        var savedToken = refreshTokenService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰 없음"));

        if (!savedToken.getToken().equals(refreshToken)) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(403, "리프레시 토큰 불일치", null));
        }

        // 5. 사용자 정보로 새 Access Token 발급
        User user = userService.findUserByEmail(email);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        TokenResponse tokenResponse = new TokenResponse(newAccessToken);

        // 6. 클라이언트에 새 토큰 반환
        return ResponseEntity.ok(new ApiResponse<>(200, "토큰 재발급 성공", tokenResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
        SignupResponse result = userService.registerUser(request);
        ApiResponse<SignupResponse> apiResponse = new ApiResponse<>(200, "회원가입 성공", result);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse result = userService.login(request, response);
        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(200, "로그인 성공", result);
        return ResponseEntity.ok(apiResponse);
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
