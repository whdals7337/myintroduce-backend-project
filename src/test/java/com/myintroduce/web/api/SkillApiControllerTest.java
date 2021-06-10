package com.myintroduce.web.api;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.domain.entity.user.User;
import com.myintroduce.domain.entity.user.UserRole;
import com.myintroduce.repository.member.MemberRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SkillApiControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private SkillRepository skillRepository;
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
        skillRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void save_skill() throws Exception {
        MockMultipartFile testFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        String skillName = "스킬이름";
        int skillLevel = 3;
        Integer level = 1;
        Long memberId = givenMember().getId();

        String url = "http://localhost:" + port + "/api/skill";

        this.mockMvc.perform(multipart(url)
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("skillName", skillName)
                .param("skillLevel", String.valueOf(skillLevel))
                .param("level", String.valueOf(level))
                .param("memberId", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.skill_name").value(skillName))
                .andExpect(jsonPath("$.data.file_origin_name").value(Objects.requireNonNull(testFile.getOriginalFilename())))
                .andExpect(jsonPath("$.data.skill_level").value(skillLevel))
                .andExpect(jsonPath("$.data.level").value(level))
                .andExpect(jsonPath("$.data.member_id").value(memberId));

        List<Skill> all = skillRepository.findAll();
        assertThat(all.get(0).getSkillName()).isEqualTo(skillName);
        assertThat(all.get(0).getFileInfo().getFileOriginName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(all.get(0).getSkillLevel()).isEqualTo(skillLevel);
        assertThat(all.get(0).getLevel()).isEqualTo(level);
        assertThat(all.get(0).getMember().getId()).isEqualTo(memberId);
    }

    @Test
    public void save_skill_without_file() throws Exception {
        String skillName = "스킬이름";
        int skillLevel = 3;
        Integer level = 1;
        Long memberId = givenMember().getId();

        String url = "http://localhost:" + port + "/api/skill";

        this.mockMvc.perform(multipart(url)
                .header("Authorization", "Bearer "+token)
                .param("skillName", skillName)
                .param("skillLevel", String.valueOf(skillLevel))
                .param("level", String.valueOf(level))
                .param("memberId", String.valueOf(memberId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("file 의 값은 필수 입니다."));
    }

    @Test
    public void update_skill() throws Exception{
        Long updateId = null;
        Member member = givenMember();

        // 스킬 값 4개 setting
        for(int i = 1; i < 5; i++){
            Skill skill = skillRepository.save(Skill.builder()
                    .skillName("스킬 이름0" + i)
                    .fileInfo(new FileInfo( "스킬 이미지 이름0" + i, "파일 주소"))
                    .skillLevel(3)
                    .level(i)
                    .member(member)
                    .build());

            // 스킬 level 이 3 인 경우의 id값
            if(i == 3) {
                updateId = skill.getId();
            }
        }

        MockMultipartFile testFile
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        String expectedSkillName = "skillName";
        int expectedSkillLevel = 1;
        int expectedLevel = 1;
        Long expectedMemberId = member.getId();

        String url = "http://localhost:" + port + "/api/skill/" + updateId;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mockMvc.perform(builder
                .file(testFile)
                .header("Authorization", "Bearer "+token)
                .param("skillName", expectedSkillName)
                .param("skillLevel", String.valueOf(expectedSkillLevel))
                .param("level", String.valueOf(expectedLevel))
                .param("memberId", String.valueOf(expectedMemberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.skill_name").value(expectedSkillName))
                .andExpect(jsonPath("$.data.file_origin_name").value(Objects.requireNonNull(testFile.getOriginalFilename())))
                .andExpect(jsonPath("$.data.skill_level").value(expectedSkillLevel))
                .andExpect(jsonPath("$.data.level").value(expectedLevel))
                .andExpect(jsonPath("$.data.member_id").value(expectedMemberId));

        // 수정된 값 검증
        assert updateId != null;
        assertThat(updateId).isGreaterThan(0L);
        Skill target = skillRepository.findById(updateId).get();
        assertThat(target.getSkillName()).isEqualTo(expectedSkillName);
        assertThat(target.getFileInfo().getFileOriginName()).isEqualTo(testFile.getOriginalFilename());
        assertThat(target.getSkillLevel()).isEqualTo(expectedSkillLevel);
        assertThat(target.getLevel()).isEqualTo(expectedLevel);
        assertThat(target.getMember().getId()).isEqualTo(expectedMemberId);
    }

    @Test
    public void update_skill_without_file() throws Exception{
        Long updateId = null;
        Member member = givenMember();
        Skill expectedSkill = null;

        // 스킬 값 4개 setting
        for(int i = 1; i < 5; i++){
            Skill skill = skillRepository.save(Skill.builder()
                    .skillName("스킬 이름0" + i)
                    .fileInfo(new FileInfo( "스킬 이미지 이름0" + i, "파일 주소"))
                    .skillLevel(3)
                    .level(i)
                    .member(member)
                    .build());

            // 스킬 level 이 3 인 경우의 id값
            if(i == 3) {
                expectedSkill = skill;
                updateId = expectedSkill.getId();
            }
        }

        String expectedSkillName = "skillName";
        int expectedSkillLevel = 1;
        int expectedLevel = 1;
        Long expectedMemberId = member.getId();

        String url = "http://localhost:" + port + "/api/skill/" + updateId;

        MockMultipartHttpServletRequestBuilder builder =
                multipart(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mockMvc.perform(builder
                .header("Authorization", "Bearer "+token)
                .param("skillName", expectedSkillName)
                .param("skillLevel", String.valueOf(expectedSkillLevel))
                .param("level", String.valueOf(expectedLevel))
                .param("memberId", String.valueOf(expectedMemberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.skill_name").value(expectedSkillName))
                .andExpect(jsonPath("$.data.file_origin_name").value(expectedSkill.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(expectedSkill.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.skill_level").value(expectedSkillLevel))
                .andExpect(jsonPath("$.data.level").value(expectedLevel))
                .andExpect(jsonPath("$.data.member_id").value(expectedMemberId));

        // 수정된 값 검증
        assert updateId != null;
        assertThat(updateId).isGreaterThan(0L);
        Skill target = skillRepository.findById(updateId).get();
        assertThat(target.getSkillName()).isEqualTo(expectedSkillName);
        assertThat(target.getFileInfo().getFileOriginName()).isEqualTo(expectedSkill.getFileInfo().getFileOriginName());
        assertThat(target.getFileInfo().getFileUrl()).isEqualTo(expectedSkill.getFileInfo().getFileUrl());
        assertThat(target.getSkillLevel()).isEqualTo(expectedSkillLevel);
        assertThat(target.getLevel()).isEqualTo(expectedLevel);
        assertThat(target.getMember().getId()).isEqualTo(expectedMemberId);
    }

    @Test
    public void update_skill_with_wrong_id() throws Exception{
        String url = "http://localhost:" + port + "/api/skill/" + 404;

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
                .andExpect(jsonPath("$.msg").value("Skill Entity가 존재하지 않습니다."));
    }

    @Test
    public void delete_skill() throws Exception {
        Member member = givenMember();
        Skill skill = givenSkill(member);

        String url = "http://localhost:" + port + "/api/skill/" + skill.getId();

        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));

        assertThat(skillRepository.findById(skill.getId())).isEmpty();
    }

    @Test
    public void delete_skill_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/skill/" + 404;

        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Skill Entity가 존재하지 않습니다."));
    }

    @Test
    public void find_skill() throws Exception {
        Member member = givenMember();
        Skill skill = givenSkill(member);

        String url = "http://localhost:" + port + "/api/skill/" + skill.getId();

        mockMvc.perform(get(url)
                .header("Authorization", "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.skill_name").value(skill.getSkillName()))
                .andExpect(jsonPath("$.data.file_origin_name").value(skill.getFileInfo().getFileOriginName()))
                .andExpect(jsonPath("$.data.file_url").value(skill.getFileInfo().getFileUrl()))
                .andExpect(jsonPath("$.data.skill_level").value(skill.getSkillLevel()))
                .andExpect(jsonPath("$.data.level").value(skill.getLevel()))
                .andExpect(jsonPath("$.data.member_id").value(member.getId()));
    }

    @Test
    public void find_skill_with_wrong_id() throws Exception {
        String url = "http://localhost:" + port + "/api/skill/" + 404;

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("Skill Entity가 존재하지 않습니다."));
    }

    @Test
    public void find_all_project() throws Exception {
        Member member = givenMember();
        int size = 6;
        for(int i = 0; i < size; i++){
            givenSkill(member);
        }

        String url = "http://localhost:" + port + "/api/skill/";

        mockMvc.perform(get(url)
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].skill_name").value("스킬 이름0"))
                .andExpect(jsonPath("$.data.[1].skill_name").value("스킬 이름0"))
                .andExpect(jsonPath("$.pagination.total_pages").value(3))
                .andExpect(jsonPath("$.pagination.total_elements").value(size))
                .andExpect(jsonPath("$.pagination.current_elements").value(2));
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

    private Skill givenSkill(Member member) {
        return skillRepository.save(Skill.builder()
                .skillName("스킬 이름0")
                .fileInfo(new FileInfo( "스킬 이미지 이름0", member.getFileInfo().getFileUrl()))
                .skillLevel(3)
                .level(1)
                .member(member)
                .build());
    }
}