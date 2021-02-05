package com.myintroduce.web.dto.skill;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequestDto {
    private String skillName;
    private Integer skillLevel;
    private Integer level;
    private Long memberId;

    public Skill toEntity(Member member, String filePath, String fileOriginName, String fileUrl) {
        return Skill.builder()
                .skillName(skillName)
                .fileInfo(new FileInfo(filePath, fileOriginName, fileUrl))
                .skillLevel(skillLevel)
                .level(level)
                .member(member)
                .build();
    }
}
