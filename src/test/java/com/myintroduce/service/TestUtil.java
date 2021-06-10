package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class TestUtil {

    public static Member mockMember(Long id, String selectYN) {
        return Member.builder()
                .id(id)
                .comment("comment")
                .fileInfo(new FileInfo("hello.txt", "fileUrl.com/qwe"))
                .subIntroduction("subIntroduction")
                .introduction("introduction")
                .phoneNumber("phoneNumber")
                .email("email")
                .selectYN(selectYN)
                .build();
    }

    public static MockMultipartFile mockFile() {
        return new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
    }
}
