package com.myintroduce.error.exception.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class FileNotTransferException extends IOException {
    private static final String MESSAGE = "파일 변환에 실패하였습니다.";

    public FileNotTransferException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}
