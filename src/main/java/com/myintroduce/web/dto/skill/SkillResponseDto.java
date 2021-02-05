package com.myintroduce.web.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillResponseDto {
    private Long skillId;
    private String skillName;
    private String fileUrl;
    private String fileOriginName;
    private Integer skillLevel;
    private Integer level;
    private Long memberId;
}
