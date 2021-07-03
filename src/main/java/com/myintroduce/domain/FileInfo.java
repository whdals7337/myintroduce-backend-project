package com.myintroduce.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileInfo {

    @Column(length = 100, nullable = false)
    private String fileOriginName;

    @Column(length = 500, nullable = false)
    private String fileUrl;

    public String s3key() {
        return fileUrl.substring(fileUrl.lastIndexOf(".com/") + 5);
    }
}
