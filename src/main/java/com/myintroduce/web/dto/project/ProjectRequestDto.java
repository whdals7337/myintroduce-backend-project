package com.myintroduce.web.dto.project;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDto {

    @ApiParam(value = "프로젝트명", required = true, example = "자기소개 페이지 프로젝트")
    private String projectTitle;

    @ApiParam(value = "프로젝트 내용", required = true, example = "자기소개 페이지는 rest api 형태로 개발 되었습니다.")
    private String projectContent;

    @ApiParam(value = "프로젝트 추신", required = true, example = "#AWS #SPRINGBOOT #REACT")
    private String projectPostScript;

    @ApiParam(value = "프로젝트 링크", required = true, example = "https://github.com/whdals7337/my-introduce")
    private String projectLink;

    @ApiParam(value = "프로젝트 순서", required = true, example = "1")
    private Integer level;

    @ApiParam(value = "프로젝트 멤버", required = true, example = "15")
    private Long memberId;


    public Project toEntity(FileInfo fileInfo, Member member) {
        return Project.builder()
                .projectTitle(projectTitle)
                .projectContent(projectContent)
                .projectPostScript(projectPostScript)
                .fileInfo(new FileInfo(fileInfo.getFilePath(), fileInfo.getFileOriginName(), fileInfo.getFileUrl()))
                .projectLink(projectLink)
                .level(level)
                .member(member)
                .build();
    }
}
