package com.myintroduce.error.exception.file;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NoSupportedBrowserException extends IOException {
    private static final String MESSAGE = "지원하지 않는 브라우저 입니다.";

    public NoSupportedBrowserException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}

