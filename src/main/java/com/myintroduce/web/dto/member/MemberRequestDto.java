package com.myintroduce.web.dto.member;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {
    private String comment;
    private String subIntroduction;
    private String introduction;
    private String phoneNumber;
    private String email;

    public Member toEntity(String filePath, String originalName, String fileUrl, String selectYN) {
        return  Member.builder()
                .comment(comment)
                .fileInfo(new FileInfo(filePath, originalName, fileUrl))
                .subIntroduction(subIntroduction)
                .introduction(introduction)
                .phoneNumber(phoneNumber)
                .email(email)
                .selectYN(selectYN)
                .build();
    }
}
