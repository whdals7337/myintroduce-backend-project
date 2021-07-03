package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.project.ProjectNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.project.ProjectRepository;
import com.myintroduce.uploader.Uploader;
import com.myintroduce.web.dto.project.ProjectRequestDto;
import com.myintroduce.web.dto.project.ProjectResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private Uploader uploader;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(uploader, memberRepository);
        projectService.baseRepository = projectRepository;
        ReflectionTestUtils.setField(projectService, "fileUploadPath","test");
        ReflectionTestUtils.setField(projectService, "subFileUploadPath", "project");
    }

    @Test
    void saveWithFile() throws IOException {
        Member member = TestUtil.mockMember(1L, "N");

        given(projectRepository.save(any(Project.class))).willReturn(mockProject(member, 1L, 1));
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));

        Header<ProjectResponseDto> target = projectService.save(mockProjectRequestDto(1L, 1), TestUtil.mockFile());

        assertThat(target.getStatus()).isEqualTo("200");

        ProjectResponseDto data = target.getData();
        Project project = mockProject(member, 1L, 1);
        validAll(data, project);
    }

    @Test
    void updateWithFile() throws IOException {
        Member member = TestUtil.mockMember(1L, "N");

        given(projectRepository.findById(1L)).willReturn(Optional.of(mockProject(member, 1L, 1)));
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header target = projectService.update(mockProjectRequestDto(1L, 1), 1L,  TestUtil.mockFile());

        assertThat(target.getStatus()).isEqualTo("200");

        ProjectResponseDto data = (ProjectResponseDto) target.getData();
        Project project = mockProject(member, 1L, 1);
        validNotFile(data, project);
        assertThat(data.getFileOriginName()).isEqualTo("test.txt");
    }

    @Test
    void updateWithoutFile() throws IOException {
        Member member = TestUtil.mockMember(1L, "N");

        given(projectRepository.findById(1L)).willReturn(Optional.of(mockProject(member, 1L, 1)));
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header target = projectService.update(mockProjectRequestDto(1L, 1), 1L,  null);

        assertThat(target.getStatus()).isEqualTo("200");

        ProjectResponseDto data = (ProjectResponseDto) target.getData();
        Project project = mockProject(member, 1L, 1);
        validAll(data, project);
    }

    @Test
    void updateDiffLevel() throws IOException {
        Member member = TestUtil.mockMember(1L, "N");
        List<Project> list = new ArrayList<>();
        list.add(mockProject(member, 2L, 2));
        list.add(mockProject(member, 3L, 3));

        given(projectRepository.findById(1L)).willReturn(Optional.of(mockProject(member, 1L, 1)));
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));
        given(projectRepository.findByLevelBetween(2,3)).willReturn(list);

        Header target = projectService.update(mockProjectRequestDto(1L, 3), 1L,  null);

        assertThat(target.getStatus()).isEqualTo("200");

        int i = 1;
        for(Project pro : list) {
            assertThat(pro.getLevel()).isEqualTo(i);
            i++;
        }

        ProjectResponseDto data = (ProjectResponseDto) target.getData();
        assertThat(data.getLevel()).isEqualTo(3);
    }


    @Test
    void updateNotFoundProject() {
        ProjectRequestDto projectRequestDto = mockProjectRequestDto(1L, 1);
        MockMultipartFile mockMultipartFile = TestUtil.mockFile();
        given(projectRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(ProjectNotFoundException.class)
                .isThrownBy(() -> projectService.update(projectRequestDto, 1L, mockMultipartFile))
                .withMessage("Project Entity가 존재하지 않습니다.");
    }

    @Test
    void delete() {
        given(projectRepository.findById(1L))
                .willReturn(Optional.of(mockProject(TestUtil.mockMember(1L, "N"),1L, 3)));
        Header target = projectService.delete(1L);
        assertThat(target.getStatus()).isEqualTo("200");
    }

    @Test
    void deleteNotFoundProject() {
        given(projectRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(ProjectNotFoundException.class)
                .isThrownBy(() -> projectService.delete(1L))
                .withMessage("Project Entity가 존재하지 않습니다.");
    }

    @Test
    void findById() {
        Member member = TestUtil.mockMember(1L, "N");
        given(projectRepository.findById(1L)).willReturn(Optional.of(mockProject(member, 1L, 1)));
        Header<ProjectResponseDto> target = projectService.findById(1L);

        assertThat(target.getStatus()).isEqualTo("200");

        ProjectResponseDto data = target.getData();
        Project project = mockProject(member, 1L, 1);
        validAll(data, project);
    }

    @Test
    void findByIdNotFoundProject () {
        given(projectRepository.findById(1L)).willReturn(Optional.empty());
        assertThatExceptionOfType(ProjectNotFoundException.class)
                .isThrownBy(() -> projectService.findById(1L))
                .withMessage("Project Entity가 존재하지 않습니다.");
    }

    @Test
    void findAll() {
        Member member = TestUtil.mockMember(1L, "N");
        List<Project> list = new ArrayList<>();
        list.add(mockProject(member, 1L, 1));
        list.add(mockProject(member, 2L, 1));
        list.add(mockProject(member, 3L, 1));
        list.add(mockProject(member, 4L, 1));

        given(projectRepository.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(list));

        Header<List<ProjectResponseDto>> target = projectService
                .findAll(PageRequest.of(0,4));

        List<ProjectResponseDto> projectResponseDtoList = target.getData();
        int i = 0;
        for(ProjectResponseDto data : projectResponseDtoList) {
            validAll(data, list.get(i));
            i++;
        }

        Pagination pagination = target.getPagination();
        assertAll("pagination",
                () -> assertThat(pagination.getTotalPages()).isEqualTo(1),
                () -> assertThat(pagination.getTotalElements()).isEqualTo(4),
                () -> assertThat(pagination.getCurrentPage()).isZero(),
                () -> assertThat(pagination.getCurrentElements()).isEqualTo(4)
        );
    }

    private void validAll(ProjectResponseDto data, Project project) {
        assertAll("projectValidAll",
                () -> assertThat(data.getProjectId()).isEqualTo(project.getId()),
                () -> assertThat(data.getProjectTitle()).isEqualTo(project.getProjectTitle()),
                () -> assertThat(data.getProjectContent()).isEqualTo(project.getProjectContent()),
                () -> assertThat(data.getProjectPostScript()).isEqualTo(project.getProjectPostScript()),
                () -> assertThat(data.getProjectLink()).isEqualTo(project.getProjectLink()),
                () -> assertThat(data.getFileOriginName()).isEqualTo(project.getFileInfo().getFileOriginName()),
                () -> assertThat(data.getFileUrl()).isEqualTo(project.getFileInfo().getFileUrl()),
                () -> assertThat(data.getLevel()).isEqualTo(project.getLevel())
        );
    }

    private void validNotFile(ProjectResponseDto data, Project project) {
        assertAll("projectValidAll",
                () -> assertThat(data.getProjectId()).isEqualTo(project.getId()),
                () -> assertThat(data.getProjectTitle()).isEqualTo(project.getProjectTitle()),
                () -> assertThat(data.getProjectContent()).isEqualTo(project.getProjectContent()),
                () -> assertThat(data.getProjectPostScript()).isEqualTo(project.getProjectPostScript()),
                () -> assertThat(data.getProjectLink()).isEqualTo(project.getProjectLink()),
                () -> assertThat(data.getLevel()).isEqualTo(project.getLevel())
        );
    }

    private ProjectRequestDto mockProjectRequestDto (Long memberId, int level) {
        return ProjectRequestDto.builder()
                .projectTitle("projectTitle")
                .projectContent("projectContent")
                .projectPostScript("projectPostScript")
                .projectLink("projectLink")
                .level(level)
                .memberId(memberId)
                .build();
    }

    private Project mockProject(Member member, Long id, int level) {
        return Project.builder()
                .id(id)
                .projectTitle("projectTitle")
                .projectContent("projectContent")
                .projectPostScript("projectPostScript")
                .fileInfo(new FileInfo( "fileOriginName", "fileUrl"))
                .projectLink("projectLink")
                .level(level)
                .member(member)
                .build();
    }
}