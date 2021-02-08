package com.myintroduce.domain.network;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private String status;
    private String msg;
    private T data;
    private Pagination pagination;

    public static <T> Header<T> OK() {
        return (Header<T>) Header.builder()
                .status("200")
                .msg("success")
                .build();
    }

    public static <T> Header<T> OK(T data) {
        return (Header<T>) Header.builder()
                .status("200")
                .msg("success")
                .data(data)
                .build();
    }

    public static <T> Header<T> OK(T data, Pagination pagination) {
        return (Header<T>) Header.builder()
                .status("200")
                .msg("success")
                .data(data)
                .pagination(pagination)
                .build();
    }
}
