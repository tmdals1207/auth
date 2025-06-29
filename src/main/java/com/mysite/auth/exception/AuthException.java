package com.mysite.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthException implements BaseException {

    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "리프레시 토큰이 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getExceptionName() {
        return this.name();
    }

    @Override
    public HttpStatus getHttpStatus() {

        return this.httpStatus;
    }

    @Override
    public String getMessage() {

        return this.message;
    }
}
