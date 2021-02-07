package com.myintroduce.web.api;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileApiControllerTest {

    @LocalServerPort
    private int port;
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
        memberRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void no_member() throws Exception {
        String url = "http://localhost:" + port + "/api/download/member/1";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Member Entity가 존재하지 않습니다."));
    }

    @Test
    public void no_skill() throws Exception {
        String url = "http://localhost:" + port + "/api/download/skill/1";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Skill Entity가 존재하지 않습니다."));
    }

    @Test
    public void no_project() throws Exception {
        String url = "http://localhost:" + port + "/api/download/project/1";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Project Entity가 존재하지 않습니다."));
    }

    @Test
    public void wrong_type() throws Exception {
        Member member = givenMember();
        String url = "http://localhost:" + port + "/api/download/wrong/1";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("존재하지않는 파일입니다."));
    }

    @Test
    public void member_download() throws Exception {
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

        this.mockMvc.perform(multipart(url)
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("comment", comment)
                .param("subIntroduction", sub_introduction)
                .param("introduction", introduction)
                .param("phoneNumber", phone_number)
                .param("email", email));

        List<Member> all = memberRepository.findAll();

        Member member = all.get(0);

        String fileUrl = "http://localhost:" + port + "/api/download/member/" + member.getId();

        mockMvc.perform(get(fileUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, World!")));
    }

    public Member givenMember() {
        return memberRepository.save(Member.builder()
                .comment("코멘트")
                .fileInfo(new FileInfo("헤어 이미지 경로","헤더 이미지 원본 이름","파일 주소"))
                .subIntroduction("서브 자기소개")
                .introduction("자기소개")
                .phoneNumber("연락처")
                .email("이메일")
                .selectYN("N")
                .build());
    }
}