package com.mysite.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(email + " 은 이미 가입된 이메일입니다.");
    }
}