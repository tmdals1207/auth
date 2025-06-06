package com.mysite.auth.oauth.info;

import com.mysite.auth.domain.enums.OAuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(OAuthProvider provider, Map<String, Object> attributes) {
        switch (provider) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
            case NAVER:
                return new NaverOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("Invalid Provider Type: " + provider);
        }
    }

}