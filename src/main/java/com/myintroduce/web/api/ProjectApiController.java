package com.myintroduce.web.api;

import com.myintroduce.repository.project.ProjectRepository;
import com.myintroduce.web.dto.project.ProjectRequestDto;
import com.myintroduce.web.dto.project.ProjectResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectApiController extends CrudWitFileController<ProjectRequestDto, ProjectResponseDto, ProjectRepository> {
}
