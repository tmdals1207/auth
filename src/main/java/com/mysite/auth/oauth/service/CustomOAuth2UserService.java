package com.mysite.auth.oauth.service;

import com.mysite.auth.domain.entity.User;
import com.mysite.auth.domain.enums.OAuthProvider;
import com.mysite.auth.domain.enums.UserRole;
import com.mysite.auth.oauth.info.OAuth2UserInfo;
import com.mysite.auth.oauth.info.OAuth2UserInfoFactory;
import com.mysite.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 시도: provider = {}", provider);
        log.info("attributes = {}", oAuth2User.getAttributes());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
        String email = userInfo.getEmail();
        log.info("email = {}", email);

        if (email == null) {
            throw new OAuth2AuthenticationException(provider.toUpperCase() + " 계정에 이메일 정보가 없습니다. 이메일 제공 동의가 필요합니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerUser(userInfo, provider));

        Map<String, Object> attributes = new HashMap<>(userInfo.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes,
                "email"
        );
    }


    private User registerUser(OAuth2UserInfo userInfo, String provider) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .profileImage(userInfo.getProfileImage())
                .provider(OAuthProvider.valueOf(provider.toUpperCase()))
                .providerId(userInfo.getProviderId())
                .role(UserRole.ROLE_USER)
                .build();

        return userRepository.save(user);
    }
}