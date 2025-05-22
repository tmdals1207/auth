package com.mysite.auth.dto.request;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String Nickname;
}