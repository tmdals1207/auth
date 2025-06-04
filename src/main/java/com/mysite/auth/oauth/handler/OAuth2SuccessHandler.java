package com.mysite.auth.oauth.handler;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.repository.UserRepository;
import com.mysite.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        String uri = request.getRequestURI();
        String providerStr = null;
        if (uri.contains("/login/oauth2/code/")) {
            providerStr = uri.substring(uri.lastIndexOf("/") + 1);
        } else {
            // Fallback: 세션이나 별도의 저장소에서 provider를 가져오도록 설계할 수도 있음
            providerStr = "unknown";
        }

        OAuthProvider provider = OAuthProvider.valueOf(providerStr.toUpperCase());

        log.info("email: {}, provider: {}", email, provider);

        User user = userRepository.findByEmailAndProvider(email,provider)
                .orElseThrow(() -> new IllegalArgumentException("OAuth 로그인 유저 DB에 없음"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenService.save(email, refreshToken);

        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        response.sendRedirect("/"); // 클라이언트 페이지
    }
}