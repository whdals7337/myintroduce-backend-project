package com.myintroduce.web.dto.member;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {

    @ApiParam(value = "대민 페이지 상단 코멘트", required = true, example = "안녕하세요. 백엔드개발자를 꿈꾸는 유종민입니다.")
    private String comment;

    @ApiParam(value = "대민페이지 Introduction 헤더", required = true , example = "개발자를 꿈꾸다!")
    private String subIntroduction;

    @ApiParam(value = "대민페이지 Introduction 상세 내용", required = true, example = "대학교에서 프로젝트를 경험하며 개발자를 꿈꾸게 되었습니다.")
    private String introduction;

    @ApiParam(value = "대민페이지 하단 연락처", required = true, example = "010-4199-8496")
    private String phoneNumber;

    @ApiParam(value = "대민페이지 하단 이메일", required = true, example = "uok0201@gmail.com")
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
