package com.myintroduce.error.exception.member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberNotFoundException extends RuntimeException{
    private static final String MESSAGE = "Member Entity가 존재하지 않습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}
