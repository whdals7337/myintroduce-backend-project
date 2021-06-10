package com.myintroduce.web.api;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.domain.entity.user.User;
import com.myintroduce.domain.entity.user.UserRole;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.project.ProjectRepository;
import com.myintroduce.repository.skill.SkillRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberApiControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SkillRepository skillRepository;
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
        skillRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void save_member() throws Exception {
        // given
        MockMultipartFile testFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        String comment = "코멘트 영역 입니다.";
        String sub_introduction = "서브 자기소개 영역입니다.";
        String introduction = "자기소개 영역입니다.";
        String phone_number = "010-1111-1111";
        String email = "uok0201@gmail.com";

        String url = "http://localhost:" + port + "/api/member";

        // when & then
        this.mockMvc.perform(multipart(url)
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("comment",comment)
                .param("subIntroduction", sub_introduction)
                .param("introduction",introduction)
                .param("phoneNumber",phone_number)
                .param("email",email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.comment").value(comment))
                .andExpect(jsonPath("$.data.file_origin_name").value(Objects.requireNonNull(testFile.getOriginalFilename())))
                .andExpect(jsonPath("$.data.sub_introduction").value(sub_introduction))
                .andExpect(jsonPath("$.data.introduction").value(introduction))
                .andExpect(jsonPath("$.data.phone_number").value(phone_number))
                .andExpect(jsonPath("$.data.select_yn").value("N"))
                .andExpect(jsonPath("$.data.email").value(email));

        List<Member> all = memberRepository.findAll();
        assertThat(all.get(0).getComment()).isEqualTo(comment);
        assertThat(all.get(0).getFileInfo().getFileOriginName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(all.get(0).getSubIntroduction()).isEqualTo(sub_introduction);
        assertThat(all.get(0).getIntroduction()).isEqualTo(introduction);
        assertThat(all.get(0).getPhoneNumber()).isEqualTo(phone_number);
        assertThat(all.get(0).getSelectYN()).isEqualTo("N");
        assertThat(all.get(0).getEmail()).isEqualTo(email);
    }

    @Test
    public void save_member_with_out_file() throws Exception {
        String comment = "코멘트 영역 입니다.";
        String sub_introduction = "서브 자기소개 영역입니다.";
        String introduction = "자기소개 영역입니다.";
        String phone_number = "010-1111-1111";
        String email = "uok0201@gmail.com";

        String url = "http://localhost:" + port + "/api/member";


        this.mockMvc.perform(multipart(url)
                .header("Authorization", "Bearer "+token)
                .param("comment",comment)
                .param("subIntroduction", sub_introduction)
                .param("introduction",introduction)
                .param("phoneNumber",phone_number)
                .param("email",email))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("file 의 값은 필수 입니다."));
    }

    @Test
    public void update_member() throws Exception {
        Long updateId = givenMember("N").getId();
        String expectedComment = "comment";
        String expectedSubIntroduction = "subIntroduction";
        String expectedIntroduction = "introduction";
        String expectedPhoneNumber = "phoneNumber";
        String expectedEmail = "email";

        MockMultipartFile testFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        String url = "http://localhost:" + port + "/api/member/" + updateId;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("comment", expectedComment)
                .param("subIntroduction", expectedSubIntroduction)
                .param("introduction", expectedIntroduction)
                .param("phoneNumber", expectedPhoneNumber)
                .param("email", expectedEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.comment").value(expectedComment))
                .andExpect(jsonPath("$.data.file_origin_name").value(Objects.requireNonNull(testFile.getOriginalFilename())))
                .andExpect(jsonPath("$.data.sub_introduction").value(expectedSubIntroduction))
                .andExpect(jsonPath("$.data.introduction").value(expectedIntroduction))
                .andExpect(jsonPath("$.data.phone_number").value(expectedPhoneNumber))
                .andExpect(jsonPath("$.data.email").value(expectedEmail));

        assertThat(updateId).isGreaterThan(0L);
        Member target = memberRepository.findById(updateId).get();
        assertThat(target.getComment()).isEqualTo(expectedComment);
        assertThat(target.getFileInfo().getFileOriginName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(target.getSubIntroduction()).isEqualTo(expectedSubIntroduction);
        assertThat(target.getIntroduction()).isEqualTo(expectedIntroduction);
        assertThat(target.getPhoneNumber()).isEqualTo(expectedPhoneNumber);
        assertThat(target.getEmail()).isEqualTo(expectedEmail);
    }

    @Test
    public void update_member_without_file() throws Exception {
        Member member = givenMember("N");
        Long updateId = member.getId();

        String expectedComment = "comment";
        String expectedSubIntroduction = "subIntroduction";
        String expectedIntroduction = "introduction";
        String expectedPhoneNumber = "phoneNumber";
        String expectedEmail = "email";

        String url = "http://localhost:" + port + "/api/member/" + member.getId();

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .header("Authorization", "Bearer "+token)
                .param("comment", expectedComment)
                .param("subIntroduction", expectedSubIntroduction)
                .param("introduction", expectedIntroduction)
                .param("phoneNumber", expectedPhoneNumber)
                .param("email", expectedEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.comment").value(expectedComment))
                .andExpect(jsonPath("$.data.file_origin_name").value(member.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(member.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.sub_introduction").value(expectedSubIntroduction))
                .andExpect(jsonPath("$.data.introduction").value(expectedIntroduction))
                .andExpect(jsonPath("$.data.phone_number").value(expectedPhoneNumber))
                .andExpect(jsonPath("$.data.email").value(expectedEmail));

        assertThat(updateId).isGreaterThan(0L);
        Member target = memberRepository.findById(updateId).get();
        assertThat(target.getComment()).isEqualTo(expectedComment);
        assertThat(target.getFileInfo().getFileOriginName()).isEqualTo(member.getFileInfo().getFileOriginName());
        assertThat(target.getFileInfo().getFileUrl()).isEqualTo(member.getFileInfo().getFileUrl());
        assertThat(target.getSubIntroduction()).isEqualTo(expectedSubIntroduction);
        assertThat(target.getIntroduction()).isEqualTo(expectedIntroduction);
        assertThat(target.getPhoneNumber()).isEqualTo(expectedPhoneNumber);
        assertThat(target.getEmail()).isEqualTo(expectedEmail);
    }

    @Test
    public void update_member_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/member/" + 404;

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
                .andExpect(jsonPath("$.msg").value("Member Entity가 존재하지 않습니다."));
    }

    @Test
    public void delete_member() throws Exception {
        Member member = givenMember("N");

        String url = "http://localhost:" + port + "/api/member/" + member.getId();

        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        assertThat(memberRepository.findById(member.getId())).isEmpty();
    }

    @Test
    public void delete_member_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/member/" + 404;

        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Member Entity가 존재하지 않습니다."));
    }

    @Test
    public void find_member() throws Exception {
        Member member = givenMember("N");

        String url = "http://localhost:" + port + "/api/member/" + member.getId();

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.comment").value(member.getComment()))
                .andExpect(jsonPath("$.data.file_origin_name").value(member.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(member.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.sub_introduction").value(member.getSubIntroduction()))
                .andExpect(jsonPath("$.data.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("$.data.phone_number").value(member.getPhoneNumber()))
                .andExpect(jsonPath("$.data.email").value(member.getEmail()));
    }

    @Test
    public void find_member_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/member/" + 404;

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Member Entity가 존재하지 않습니다."));
    }

    @Test
    public void totalInfo() throws Exception {
        Member member = givenMember("N");
        Project project = givenProject(member);
        Skill skill = givenSkill(member);

        String url = "http://localhost:" + port + "/api/member/"+member.getId() +"/totalInfo";

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.member_response_dto.member_id").value(member.getId()))
                .andExpect(jsonPath("$.data.member_response_dto.comment").value(member.getComment()))
                .andExpect(jsonPath("$.data.member_response_dto.file_url").value(member.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.member_response_dto.file_origin_name").value(member.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.member_response_dto.sub_introduction").value(member.getSubIntroduction()))
                .andExpect(jsonPath("$.data.member_response_dto.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("$.data.member_response_dto.phone_number").value(member.getPhoneNumber()))
                .andExpect(jsonPath("$.data.member_response_dto.email").value(member.getEmail()))
                .andExpect(jsonPath("$.data.member_response_dto.select_yn").value(member.getSelectYN()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].skill_id").value(skill.getId()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].skill_name").value(skill.getSkillName()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].file_url").value(skill.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].file_origin_name").value(skill.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].skill_level").value(skill.getSkillLevel()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].level").value(skill.getLevel()))
                .andExpect(jsonPath("$.data.skill_response_dto_list.[0].member_id").value(skill.getMember().getId()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].project_id").value(project.getId()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].project_title").value(project.getProjectTitle()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].project_content").value(project.getProjectContent()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].project_post_script").value(project.getProjectPostScript()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].file_url").value(project.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].file_origin_name").value(project.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].project_link").value(project.getProjectLink()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].level").value(project.getLevel()))
                .andExpect(jsonPath("$.data.project_response_dto_list.[0].member_id").value(project.getMember().getId()));
    }

    @Test
    public void totalInfo_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/member/"+ 404 +"/totalInfo";

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Member Entity가 존재하지 않습니다."));
    }

    @Test
    public void member_select() throws Exception {
        Member member = givenMember("N");
        String url = "http://localhost:" + port + "/api/member/select/"+member.getId();

        mockMvc.perform(patch(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.comment").value(member.getComment()))
                .andExpect(jsonPath("$.data.file_origin_name").value(member.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(member.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.sub_introduction").value(member.getSubIntroduction()))
                .andExpect(jsonPath("$.data.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("$.data.phone_number").value(member.getPhoneNumber()))
                .andExpect(jsonPath("$.data.email").value(member.getEmail()));

        List<Member> list = memberRepository.findAll();
        for(Member target : list){
            if(target.getId().equals(member.getId())){
                assertThat(target.getSelectYN()).isEqualTo("Y");
            }
            else {
                assertThat(target.getSelectYN()).isEqualTo("N");
            }
        }
    }

    @Test
    public void find_all_member() throws Exception {
        int size = 6;
        for(int i = 0; i < size; i++){
            givenMember();
        }

        String url = "http://localhost:" + port + "/api/member";

        mockMvc.perform(get(url)
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.[0].comment").value("코멘트"));
    }


    @Test
    public void findBySelectYN() throws Exception {
        Member member = givenMember("Y");

        String url = "http://localhost:" + port + "/api/member/select";

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.comment").value(member.getComment()))
                .andExpect(jsonPath("$.data.file_origin_name").value(member.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(member.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.sub_introduction").value(member.getSubIntroduction()))
                .andExpect(jsonPath("$.data.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("$.data.phone_number").value(member.getPhoneNumber()))
                .andExpect(jsonPath("$.data.email").value(member.getEmail()));
    }

    @Test
    public void member_select_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/member/select/"+ 400;
        mockMvc.perform(patch(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Member Entity가 존재하지 않습니다."));
    }

    private Member givenMember(String selectYN) {
        return memberRepository.save(Member.builder()
                .comment("코멘트")
                .fileInfo(new FileInfo( "헤더 이미지 원본 이름", "파일 경로.com/qwe"))
                .subIntroduction("서브 자기소개")
                .introduction("자기소개")
                .phoneNumber("연락처")
                .email("이메일")
                .selectYN(selectYN)
                .build());
    }

    private Member givenMember() {
        return memberRepository.save(Member.builder()
                .comment("코멘트")
                .fileInfo(new FileInfo( "헤더 이미지 원본 이름", "파일 경로.com/qwe"))
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
                .projectPostScript("프로젝트 추신0")
                .fileInfo(new FileInfo( "프로젝트 이미지 원본이름0", "파일주소.com/qwe"))
                .projectLink("http://gergerg")
                .level(1)
                .member(member)
                .build());
    }

    private Skill givenSkill(Member member) {
        return skillRepository.save(Skill.builder()
                .skillName("스킬 이름0")
                .fileInfo(new FileInfo( "스킬 이미지 이름0", "주소.com/fwe"))
                .skillLevel(3)
                .level(1)
                .member(member)
                .build());
    }
}