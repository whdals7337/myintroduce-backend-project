package com.myintroduce.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private Long memberId;
    private String comment;
    private String fileUrl;
    private String fileOriginName;
    private String subIntroduction;
    private String introduction;
    private String phoneNumber;
    private String email;
    private String selectYN;
}
