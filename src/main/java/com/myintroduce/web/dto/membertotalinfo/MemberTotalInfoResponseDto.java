package com.myintroduce.web.dto.membertotalinfo;

import com.myintroduce.web.dto.member.MemberResponseDto;
import com.myintroduce.web.dto.project.ProjectResponseDto;
import com.myintroduce.web.dto.skill.SkillResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTotalInfoResponseDto {

    private MemberResponseDto memberResponseDto;
    private List<SkillResponseDto> skillResponseDtoList;
    private List<ProjectResponseDto> projectResponseDtoList;
}
