package com.myintroduce.web.dto.project;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDto {
    private String projectTitle;
    private String projectContent;
    private String projectPostScript;
    private String projectLink;
    private Integer level;
    private Long memberId;

    public Project toEntity(Member member, String filePath, String fileOriginName, String fileUrl) {
        return Project.builder()
                .projectTitle(projectTitle)
                .projectContent(projectContent)
                .projectPostScript(projectPostScript)
                .fileInfo(new FileInfo(filePath, fileOriginName, fileUrl))
                .projectLink(projectLink)
                .level(level)
                .member(member)
                .build();
    }
}
