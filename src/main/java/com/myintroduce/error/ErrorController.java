package com.myintroduce.error;

import com.myintroduce.error.exception.file.FileNotRequestException;
import com.myintroduce.error.exception.file.FileNotTransferException;
import com.myintroduce.error.exception.file.NoSupportedBrowserException;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.error.exception.project.ProjectNotFoundException;
import com.myintroduce.error.exception.session.LoginInfoWrongException;
import com.myintroduce.error.exception.skill.SkillNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MemberNotFoundException.class)
    public ErrorMsg handleMemberNotFoundException(MemberNotFoundException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ProjectNotFoundException.class)
    public ErrorMsg handleProjectNotFoundException(ProjectNotFoundException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = SkillNotFoundException.class)
    public ErrorMsg handleSkillNotFoundException(SkillNotFoundException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = FileNotFoundException.class)
    public ErrorMsg handleFileNotFoundException(FileNotFoundException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = FileNotTransferException.class)
    public ErrorMsg handleIOException(FileNotTransferException e) {
        return new ErrorMsg(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = UnsupportedEncodingException.class)
    public ErrorMsg handleUnsupportedEncodingException(IOException e) {
        return new ErrorMsg(HttpStatus.INTERNAL_SERVER_ERROR, "인코딩에 실패하였습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMsg handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder builder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            builder.append("\n");
        }
        return new ErrorMsg(HttpStatus.BAD_REQUEST, builder.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileNotRequestException.class)
    public ErrorMsg handleFileNotRequestException(FileNotRequestException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ErrorMsg handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getRequestPartName() + " 의 값은 필수 입니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginInfoWrongException.class)
    public ErrorMsg handleLoginInfoWrongException(LoginInfoWrongException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSupportedBrowserException.class)
    public ErrorMsg handleNoSupportedBrowserException(NoSupportedBrowserException e) {
        return new ErrorMsg(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = IOException.class)
    public ErrorMsg handleIOException(IOException e) {
        return new ErrorMsg(HttpStatus.INTERNAL_SERVER_ERROR, "파일 관련 예상하지 못한 에러가 발생하였습니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RuntimeException.class)
    public ErrorMsg handleRuntimeException(RuntimeException e) {
        return new ErrorMsg(HttpStatus.INTERNAL_SERVER_ERROR, "예상하지 못한 에러가 발생하였습니다.");
    }
}
