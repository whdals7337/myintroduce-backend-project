package com.myintroduce.error.exception.project;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectNotFoundException extends RuntimeException{
    private static final String MESSAGE = "Project Entity가 존재하지 않습니다.";

    public ProjectNotFoundException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}
