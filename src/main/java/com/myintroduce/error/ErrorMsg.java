package com.myintroduce.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ErrorMsg {

    private int status;
    private String msg;

    public ErrorMsg(HttpStatus status, String msg) {
        this.status = status.value();
        this.msg = msg;
    }
}
