package com.mysite.auth.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super(email + " 에 해당하는 사용자를 찾을 수 없습니다: ");
    }
}
