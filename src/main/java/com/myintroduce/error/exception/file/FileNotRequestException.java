package com.myintroduce.error.exception.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class FileNotRequestException extends IOException {
    private static final String MESSAGE = "file 의 값은 필수 입니다.";

    public FileNotRequestException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}
