package com.myintroduce.web.api;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.user.User;
import com.myintroduce.domain.entity.user.UserRole;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.project.ProjectRepository;
import com.myintroduce.repository.user.UserRepository;
import com.myintroduce.utill.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectApiControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        userRepository.save(User.builder()
                .username("ADMIN")
                .password("qweqwe")
                .role(UserRole.ADMIN)
                .build());
        token = jwtUtil.createToken(1004L, "ADMIN");
    }

    @AfterEach
    public void cleanUp() {
        projectRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void save_project() throws Exception {
        MockMultipartFile testFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        String projectTitle = "프로젝트 이름";
        String projectContent = "프로젝트 내용";
        String projectPostScript = "프로젝트 추신";
        String projectLink = "http://gaergerg";
        int level = 1;
        Long memberId = givenMember().getId();

        String url = "http://localhost:" + port + "/api/project";

        mockMvc.perform(multipart(url)
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("projectTitle", projectTitle)
                .param("projectContent", projectContent)
                .param("projectPostScript", projectPostScript)
                .param("projectLink", projectLink)
                .param("level", String.valueOf(level))
                .param("memberId", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.project_title").value(projectTitle))
                .andExpect(jsonPath("$.data.file_origin_name").value(Objects.requireNonNull(testFile.getOriginalFilename())))
                .andExpect(jsonPath("$.data.project_content").value(projectContent))
                .andExpect(jsonPath("$.data.project_post_script").value(projectPostScript))
                .andExpect(jsonPath("$.data.project_link").value(projectLink))
                .andExpect(jsonPath("$.data.level").value(level))
                .andExpect(jsonPath("$.data.member_id").value(memberId));

        List<Project> all = projectRepository.findAll();
        assertThat(all.get(0).getProjectTitle()).isEqualTo(projectTitle);
        assertThat(all.get(0).getFileInfo().getFileOriginName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(all.get(0).getProjectContent()).isEqualTo(projectContent);
        assertThat(all.get(0).getProjectPostScript()).isEqualTo(projectPostScript);
        assertThat(all.get(0).getProjectLink()).isEqualTo(projectLink);
        assertThat(all.get(0).getLevel()).isEqualTo(level);
        assertThat(all.get(0).getMember().getId()).isEqualTo(memberId);
    }

    @Test
    public void save_project_without_file() throws Exception {
        String projectTitle = "프로젝트 이름";
        String projectContent = "프로젝트 내용";
        String projectPostScript = "프로젝트 추신";
        String projectLink = "http://gaergerg";
        Integer level = 1;
        Long memberId = givenMember().getId();

        String url = "http://localhost:" + port + "/api/project";

        mockMvc.perform(multipart(url)
                .header("Authorization", "Bearer "+token)
                .param("projectTitle", projectTitle)
                .param("projectContent", projectContent)
                .param("projectPostScript", projectPostScript)
                .param("projectLink", projectLink)
                .param("level", String.valueOf(level))
                .param("memberId", String.valueOf(memberId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("file 의 값은 필수 입니다."));
    }

    @Test
    public void update_project() throws Exception{
        Long updateId = null;
        // level 의 3과 1을 교환한 순서
        int[] level_list = {2, 3, 1 ,4};
        Member member = givenMember();

        // 프로젝트 값 4개 setting
        for(int i = 1; i < 5; i++){
            Project project = projectRepository.save(Project.builder()
                    .projectTitle("프로젝트 이름0" + i)
                    .projectContent("프로젝트 내용0" + i)
                    .projectPostScript("프로젝트 추신0" + i)
                    .fileInfo(new FileInfo("프로젝트 이미지 경로0" + i, "프로젝트 이미지 원본이름0" + i, "파일 주소" ))
                    .projectLink("http://gergerg" + i)
                    .level(i)
                    .member(member)
                    .build());

            // 프로젝트 level 이 3 인 경우의 id값
            if(i == 3) {
                updateId = project.getId();
            }
        }

        MockMultipartFile testFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        String expectedProjectTitle = "projectTitle";
        String expectedProjectContent = "projectContent";
        String expectedProjectPostScript = "projectPostScript";
        String expectedProjectLink = "http://gergergerg";
        int expectedLevel = 1;
        Long expectedMemberId = member.getId();

        String url = "http://localhost:" + port + "/api/project/" + updateId;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("projectTitle", expectedProjectTitle)
                .param("projectContent", expectedProjectContent)
                .param("projectPostScript", expectedProjectPostScript)
                .param("projectLink", expectedProjectLink)
                .param("level", String.valueOf(expectedLevel))
                .param("memberId", String.valueOf(expectedMemberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.project_title").value(expectedProjectTitle))
                .andExpect(jsonPath("$.data.file_origin_name").value(Objects.requireNonNull(testFile.getOriginalFilename())))
                .andExpect(jsonPath("$.data.project_content").value(expectedProjectContent))
                .andExpect(jsonPath("$.data.project_post_script").value(expectedProjectPostScript))
                .andExpect(jsonPath("$.data.project_link").value(expectedProjectLink))
                .andExpect(jsonPath("$.data.level").value(expectedLevel))
                .andExpect(jsonPath("$.data.member_id").value(expectedMemberId));

        assert updateId != null;
        assertThat(updateId).isGreaterThan(0L);
        Project target = projectRepository.findById(updateId).get();
        assertThat(target.getProjectTitle()).isEqualTo(expectedProjectTitle);
        assertThat(target.getProjectContent()).isEqualTo(expectedProjectContent);
        assertThat(target.getProjectPostScript()).isEqualTo(expectedProjectPostScript);
        assertThat(target.getFileInfo().getFileOriginName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(target.getProjectLink()).isEqualTo(expectedProjectLink);
        assertThat(target.getLevel()).isEqualTo(expectedLevel);
        assertThat(target.getMember().getId()).isEqualTo(expectedMemberId);

        // 테이블의 level 값 전체 검증
        List<Project> all = projectRepository.findAll();
        for(int i = 0; i < 4; i++){
            assertThat(all.get(i).getLevel()).isEqualTo(level_list[i]);
        }
    }

    @Test
    public void update_project_without_file() throws Exception{
        Long updateId = null;
        // level 의 3과 1을 교환한 순서
        int[] level_list = {2, 3, 1 ,4};
        Member member = givenMember();
        Project expectedProject = null;

        // 프로젝트 값 4개 setting
        for(int i = 1; i < 5; i++){
            Project project = projectRepository.save(Project.builder()
                    .projectTitle("프로젝트 이름0" + i)
                    .projectContent("프로젝트 내용0" + i)
                    .projectPostScript("프로젝트 추신0" + i)
                    .fileInfo(new FileInfo("프로젝트 이미지 경로0" + i, "프로젝트 이미지 원본이름0" + i, "파일 주소" ))
                    .projectLink("http://gergerg" + i)
                    .level(i)
                    .member(member)
                    .build());

            // 프로젝트 level 이 3 인 경우의 id값
            if(i == 3) {
                expectedProject = project;
                updateId = expectedProject.getId();
            }
        }

        String expectedProjectTitle = "projectTitle";
        String expectedProjectContent = "projectContent";
        String expectedProjectPostScript = "projectPostScript";
        String expectedProjectLink = "http://gergergerg";
        int expectedLevel = 1;
        Long expectedMemberId = member.getId();

        String url = "http://localhost:" + port + "/api/project/" + updateId;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .header("Authorization", "Bearer "+token)
                .param("projectTitle", expectedProjectTitle)
                .param("projectContent", expectedProjectContent)
                .param("projectPostScript", expectedProjectPostScript)
                .param("projectLink", expectedProjectLink)
                .param("level", String.valueOf(expectedLevel))
                .param("memberId", String.valueOf(expectedMemberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.project_title").value(expectedProjectTitle))
                .andExpect(jsonPath("$.data.file_origin_name").value(expectedProject.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(expectedProject.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.project_content").value(expectedProjectContent))
                .andExpect(jsonPath("$.data.project_post_script").value(expectedProjectPostScript))
                .andExpect(jsonPath("$.data.project_link").value(expectedProjectLink))
                .andExpect(jsonPath("$.data.level").value(expectedLevel))
                .andExpect(jsonPath("$.data.member_id").value(expectedMemberId));

        assert updateId != null;
        assertThat(updateId).isGreaterThan(0L);
        Project target = projectRepository.findById(updateId).get();
        assertThat(target.getProjectTitle()).isEqualTo(expectedProjectTitle);
        assertThat(target.getProjectContent()).isEqualTo(expectedProjectContent);
        assertThat(target.getProjectPostScript()).isEqualTo(expectedProjectPostScript);
        assertThat(target.getFileInfo().getFileOriginName()).isEqualTo(expectedProject.getFileInfo().getFileOriginName());
        assertThat(target.getFileInfo().getFilePath()).isEqualTo(expectedProject.getFileInfo().getFilePath());
        assertThat(target.getFileInfo().getFileUrl()).isEqualTo(expectedProject.getFileInfo().getFileUrl());
        assertThat(target.getProjectLink()).isEqualTo(expectedProjectLink);
        assertThat(target.getLevel()).isEqualTo(expectedLevel);
        assertThat(target.getMember().getId()).isEqualTo(expectedMemberId);

        // 테이블의 level 값 전체 검증
        List<Project> all = projectRepository.findAll();
        for(int i = 0; i < 4; i++){
            assertThat(all.get(i).getLevel()).isEqualTo(level_list[i]);
        }
    }

    @Test
    public void update_project_with_wrong_id() throws Exception{
        String url = "http://localhost:" + port + "/api/project/" + 404;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Project Entity가 존재하지 않습니다."));
    }

    @Test
    public void delete_project() throws Exception {
        Member member = givenMember();
        Project project = givenProject(member);

        String url = "http://localhost:" + port + "/api/project/" + project.getId();

        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        assertThat(projectRepository.findById(project.getId())).isEmpty();
    }

    @Test
    public void delete_project_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/project/" + 404;

        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Project Entity가 존재하지 않습니다."));
    }

    @Test
    public void find_project() throws Exception {
        Member member = givenMember();
        Project project = givenProject(member);

        String url = "http://localhost:" + port + "/api/project/" + project.getId();

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.project_title").value(project.getProjectTitle()))
                .andExpect(jsonPath("$.data.file_origin_name").value(project.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(project.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.project_content").value(project.getProjectContent()))
                .andExpect(jsonPath("$.data.project_post_script").value(project.getProjectPostScript()))
                .andExpect(jsonPath("$.data.project_link").value(project.getProjectLink()))
                .andExpect(jsonPath("$.data.level").value(project.getLevel()))
                .andExpect(jsonPath("$.data.member_id").value(member.getId()));
    }

    @Test
    public void find_project_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/project/" + 404;

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Project Entity가 존재하지 않습니다."));
    }

    @Test
    public void find_all_project() throws Exception {
        Member member = givenMember();
        int size = 6;
        for(int i = 0; i < size; i++){
            givenProject(member);
        }

        String url = "http://localhost:" + port + "/api/project/";

        mockMvc.perform(get(url)
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].project_title").value("프로젝트 이름0"))
                .andExpect(jsonPath("$.data.[1].project_title").value("프로젝트 이름0"))
                .andExpect(jsonPath("$.pagination.total_pages").value(3))
                .andExpect(jsonPath("$.pagination.total_elements").value(size))
                .andExpect(jsonPath("$.pagination.current_elements").value(2));
    }

    private Member givenMember() {
        return memberRepository.save(Member.builder()
                .comment("코멘트")
                .fileInfo(new FileInfo("헤어 이미지 경로", "헤더 이미지 원본 이름", "파일 경로"))
                .subIntroduction("서브 자기소개")
                .introduction("자기소개")
                .phoneNumber("연락처")
                .email("이메일")
                .selectYN("N")
                .build());
    }

    private Project givenProject(Member member) {
        return projectRepository.save(Project.builder()
                .projectTitle("프로젝트 이름0")
                .projectContent("프로젝트 내용0")
                .fileInfo(new FileInfo("프로젝트 이미지 경로0", "프로젝트 이미지 원본이름0", "파일주소"))
                .projectPostScript("프로젝트 추신0")
                .projectLink("http://gergerg")
                .level(1)
                .member(member)
                .build());
    }
}