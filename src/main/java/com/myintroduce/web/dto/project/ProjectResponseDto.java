package com.myintroduce.web.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponseDto {
    private Long projectId;
    private String projectTitle;
    private String projectContent;
    private String projectPostScript;
    private String fileUrl;
    private String fileOriginName;
    private String projectLink;
    private Integer level;
    private Long memberId;
}
