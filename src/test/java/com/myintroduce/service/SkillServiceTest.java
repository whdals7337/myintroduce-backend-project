package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.error.exception.skill.SkillNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.skill.SkillRepository;
import com.myintroduce.web.dto.skill.SkillRequestDto;
import com.myintroduce.web.dto.skill.SkillResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    public void setUp() {
        skillService = new SkillService(memberRepository);
        skillService.baseRepository = skillRepository;
        ReflectionTestUtils.setField(skillService, "fileUploadPath","/test-dir/files/");
        ReflectionTestUtils.setField(skillService, "domain", "http://localhost:8080");
        ReflectionTestUtils.setField(skillService, "dirType", "images");
        ReflectionTestUtils.setField(skillService, "subFileUploadPath", "skill");
    }

    @Test
    public void saveWithFile() throws IOException {
        Member member = TestUtil.mockMember(1L, "N");

        given(skillRepository.save(any(Skill.class))).willReturn(mockSkill(member, 1L, 1));

        Header<SkillResponseDto> target = skillService.save(mockSkillRequestDto(1L, 1), TestUtil.mockFile());

        assertThat(target.getStatus()).isEqualTo("200");

        SkillResponseDto data = target.getData();
        Skill skill = mockSkill(member, 1L, 1);
        validAll(data, skill);
    }

    @Test
    public void updateWithFile() {
        Member member = TestUtil.mockMember(1L, "N");

        given(skillRepository.findById(1L)).willReturn(Optional.of(mockSkill(member,1L, 3)));
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));

        Header target = skillService.update(mockSkillRequestDto(1L, 1), 1L, TestUtil.mockFile());

        assertThat(target.getStatus()).isEqualTo("200");

        SkillResponseDto data = (SkillResponseDto) target.getData();
        Skill skill = mockSkill(member, 1L, 1);
        validNotFile(data, skill);
        assertThat(data.getFileOriginName()).isEqualTo("test.txt");
    }

    @Test
    public void updateWithoutFile() {
        Member member = TestUtil.mockMember(1L, "N");

        given(skillRepository.findById(1L)).willReturn(Optional.of(mockSkill(member,1L, 3)));
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));

        Header target = skillService.update(mockSkillRequestDto(1L, 1), 1L, null);

        assertThat(target.getStatus()).isEqualTo("200");

        SkillResponseDto data = (SkillResponseDto) target.getData();
        Skill skill = mockSkill(member, 1L, 1);
        validAll(data, skill);
    }

    @Test
    public void updateNotFoundSkill() {
        given(skillRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(SkillNotFoundException.class)
                .isThrownBy(() -> skillService.update(mockSkillRequestDto(1L, 1), 1L, TestUtil.mockFile()))
                .withMessage("Skill Entity가 존재하지 않습니다.");
    }

    @Test
    public void delete() {
        given(skillRepository.findById(1L))
                .willReturn(Optional.of(mockSkill(TestUtil.mockMember(1L, "N"),1L, 3)));

        Header target = skillService.delete(1L);

        assertThat(target.getStatus()).isEqualTo("200");
    }

    @Test
    public void deleteNotFoundSkill() {
        given(skillRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(SkillNotFoundException.class)
                .isThrownBy(() -> skillService.delete(1L))
                .withMessage("Skill Entity가 존재하지 않습니다.");
    }

    @Test
    public void findById() {
        Member member = TestUtil.mockMember(1L, "N");

        given(skillRepository.findById(1L)).willReturn(Optional.of(mockSkill(member, 1L, 1)));

        Header<SkillResponseDto> target = skillService.findById(1L);

        assertThat(target.getStatus()).isEqualTo("200");

        SkillResponseDto data = target.getData();
        Skill skill = mockSkill(member, 1L, 1);
        validAll(data, skill);
    }

    @Test
    public void findByIdNotFoundSkill () {
        given(skillRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(SkillNotFoundException.class)
                .isThrownBy(() -> skillService.findById(1L))
                .withMessage("Skill Entity가 존재하지 않습니다.");
    }

    @Test
    public void findAll() {
        Member member = TestUtil.mockMember(1L, "N");
        List<Skill> list = new ArrayList<>();
        list.add(mockSkill(member, 1L, 1));
        list.add(mockSkill(member, 2L, 1));
        list.add(mockSkill(member, 3L, 1));
        list.add(mockSkill(member, 4L, 1));

        given(skillRepository.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(list));

        Header<List<SkillResponseDto>> target = skillService
                .findAll(PageRequest.of(0,4));

        List<SkillResponseDto> skillResponseDtoList = target.getData();
        int i = 0;
        for(SkillResponseDto data : skillResponseDtoList) {
            validAll(data, list.get(i));
            i++;
        }

        Pagination pagination = target.getPagination();
        assertThat(pagination.getTotalPages()).isEqualTo(1);
        assertThat(pagination.getTotalElements()).isEqualTo(4);
        assertThat(pagination.getCurrentPage()).isEqualTo(0);
        assertThat(pagination.getCurrentElements()).isEqualTo(4);
    }

    @Test
    public void getSkill() {
        Member member = TestUtil.mockMember(1L, "N");
        Skill skill = mockSkill(member, 1L, 1);

        given(skillRepository.findById(1L)).willReturn(Optional.of(skill));

        Skill target = skillService.getSkill(1L);

        assertThat(target).isEqualTo(skill);
    }

    @Test
    public void getSkillNotFoundSkill() {
        given(skillRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(SkillNotFoundException.class)
                .isThrownBy(() -> skillService.getSkill(1L))
                .withMessage("Skill Entity가 존재하지 않습니다.");
    }

    private void validAll(SkillResponseDto data, Skill skill) {
        assertThat(data.getSkillId()).isEqualTo(skill.getId());
        assertThat(data.getFileOriginName()).isEqualTo(skill.getFileInfo().getFileOriginName());
        assertThat(data.getFileUrl()).isEqualTo(skill.getFileInfo().getFileUrl());
        assertThat(data.getSkillLevel()).isEqualTo(skill.getSkillLevel());
        assertThat(data.getLevel()).isEqualTo(skill.getLevel());
        assertThat(data.getSkillName()).isEqualTo(skill.getSkillName());
        assertThat(data.getMemberId()).isEqualTo(skill.getMember().getId());
    }

    private void validNotFile(SkillResponseDto data, Skill skill) {
        assertThat(data.getSkillId()).isEqualTo(skill.getId());
        assertThat(data.getSkillLevel()).isEqualTo(skill.getSkillLevel());
        assertThat(data.getLevel()).isEqualTo(skill.getLevel());
        assertThat(data.getSkillName()).isEqualTo(skill.getSkillName());
        assertThat(data.getMemberId()).isEqualTo(skill.getMember().getId());
    }

    private SkillRequestDto mockSkillRequestDto(Long memberId, int level) {
        return SkillRequestDto.builder()
                .skillName("skillName")
                .skillLevel(3)
                .level(level)
                .memberId(memberId)
                .build();
    }

    private Skill mockSkill(Member member, Long id, int level) {
        return Skill.builder()
                .id(id)
                .skillName("skillName")
                .fileInfo(new FileInfo("filePath", "fileOriginName", "fileUrl"))
                .level(level)
                .skillLevel(3)
                .member(member)
                .build();
    }
}