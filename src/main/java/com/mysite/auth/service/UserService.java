package com.mysite.auth.service;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.dto.request.LoginRequest;
import com.mysite.auth.dto.response.LoginResponse;
import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.domain.enums.UserRole;
import com.mysite.auth.exception.EmailAlreadyExistsException;
import com.mysite.auth.exception.InvalidPasswordException;
import com.mysite.auth.exception.UserNotFoundException;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.repository.UserRepository;
import com.mysite.auth.dto.request.SignupRequest;
import com.mysite.auth.dto.response.SignupResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenService.save(user.getEmail(), refreshToken);

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        return new LoginResponse(user.getNickname(), "로그인 성공");
    }

    public SignupResponse registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .provider(OAuthProvider.LOCAL)
                .providerId(null)
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);

        return new SignupResponse(user.getId(), "회원가입이 완료되었습니다.");
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}