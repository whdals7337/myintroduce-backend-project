package com.myintroduce.error.exception.skill;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SkillNotFoundException extends RuntimeException{
    private static final String MESSAGE = "Skill Entity가 존재하지 않습니다.";

    public SkillNotFoundException() {
        super(MESSAGE);
        log.error(MESSAGE);
    }
}
