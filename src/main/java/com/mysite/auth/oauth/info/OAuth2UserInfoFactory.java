package com.mysite.auth.oauth.info;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) {
            case "google":
                return new GoogleOAuth2UserInfo(attributes);
            case "kakao":
                return new KakaoOAuth2UserInfo(attributes);
            case "naver":
                return new NaverOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("Invalid Provider Type: " + provider);
        }
    }

}