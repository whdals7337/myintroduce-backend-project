package com.myintroduce.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileInfo {

    @Column(length = 500, nullable = false)
    private String filePath;

    @Column(length = 100, nullable = false)
    private String fileOriginName;

    @Column(length = 500, nullable = false)
    private String fileUrl;

    public FileInfo(String fileOriginName, String fileUrl) {
        this.fileOriginName = fileOriginName;
        this.fileUrl = fileUrl;
    }
}
