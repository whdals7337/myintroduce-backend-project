package com.myintroduce.web.dto.skill;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequestDto {

    @ApiParam(value = "스킬명", required = true, example = "JAVA")
    private String skillName;

    @ApiParam(value = "스킬숙련도", required = true, example = "2")
    private Integer skillLevel;

    @ApiParam(value = "스킬등록순서", required = true, example = "1")
    private Integer level;

    @ApiParam(value = "스킬 멤버", required = true, example = "15")
    private Long memberId;

    public Skill toEntity(FileInfo fileInfo, Member member) {
        return Skill.builder()
                .skillName(skillName)
                .fileInfo(new FileInfo(fileInfo.getFilePath(), fileInfo.getFileOriginName(), fileInfo.getFileUrl()))
                .skillLevel(skillLevel)
                .level(level)
                .member(member)
                .build();
    }
}
