package com.myintroduce.error.exception.session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginInfoWrongException extends RuntimeException {
    private static final String MESSAGE = "로그인정보가 올바르지 않습니다.";

    public LoginInfoWrongException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}
