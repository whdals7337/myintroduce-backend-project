package com.myintroduce.domain.network;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header<T> {

    private static final String SUCCESS_CODE = "200";
    private static final String SUCCESS_MESSAGE = "success";

    @ApiModelProperty(position = 1, notes = "상태코드")
    private String status;
    @ApiModelProperty(position = 2, notes = "상태 메세지")
    private String msg;
    @ApiModelProperty(position = 3, notes = "요청 데이터")
    private T data;
    @ApiModelProperty(position = 4, notes = "페이징 정보")
    private Pagination pagination;

    public static <T> Header<T> OK() {
        return (Header<T>) Header.builder()
                .status(SUCCESS_CODE)
                .msg(SUCCESS_MESSAGE)
                .data(null)
                .build();
    }

    public static <T> Header<T> OK(T data) {
        return (Header<T>) Header.builder()
                .status(SUCCESS_CODE)
                .msg(SUCCESS_MESSAGE)
                .data(data)
                .build();
    }

    public static <T> Header<T> OK(T data, Pagination pagination) {
        return (Header<T>) Header.builder()
                .status(SUCCESS_CODE)
                .msg(SUCCESS_MESSAGE)
                .data(data)
                .pagination(pagination)
                .build();
    }
}
