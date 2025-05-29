package com.mysite.auth.oauth;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.jwt.JwtTokenProvider;
import com.mysite.auth.oauth.handler.OAuth2SuccessHandler;
import com.mysite.auth.repository.UserRepository;
import com.mysite.auth.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @InjectMocks
    private OAuth2SuccessHandler successHandler;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("OAuthSuccessHandler 구동 테스트")
    void onAuthenticationSuccess_shouldSetAccessTokenCookie() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);

        DefaultOAuth2User userPrincipal = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("email", "user@example.com"),
                "email"
        );

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new User()));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("mockAccessToken");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("mockRefreshToken");

        Cookie[] resultCookies = new Cookie[1];
        doAnswer(invocation -> {
            Cookie c = invocation.getArgument(0);
            resultCookies[0] = c;
            return null;
        }).when(response).addCookie(any(Cookie.class));

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        assertThat(resultCookies[0].getName()).isEqualTo("accessToken");
        assertThat(resultCookies[0].getValue()).isEqualTo("mockAccessToken");
    }
}
