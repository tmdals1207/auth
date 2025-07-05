package com.mysite.auth.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {

    String getExceptionName();

    HttpStatus getHttpStatus();

    String getMessage();
}
