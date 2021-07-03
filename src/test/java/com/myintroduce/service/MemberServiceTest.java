package com.myintroduce.service;

import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.uploader.Uploader;
import com.myintroduce.web.dto.member.MemberRequestDto;
import com.myintroduce.web.dto.member.MemberResponseDto;
import com.myintroduce.web.dto.membertotalinfo.MemberTotalInfoResponseDto;
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
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private Uploader uploader;
    @Mock
    private SkillService skillService;
    @Mock
    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        memberService = new MemberService(uploader, skillService, projectService);
        memberService.baseRepository = memberRepository;
        ReflectionTestUtils.setField(memberService, "fileUploadPath","test");
        ReflectionTestUtils.setField(memberService, "subFileUploadPath", "member");
    }

    @Test
    void saveWithFile() throws IOException {
        given(memberRepository.save(any(Member.class))).willReturn(TestUtil.mockMember(1L, "N"));

        Header<MemberResponseDto> target = memberService.save(mockMemberRequestDto(), TestUtil.mockFile());

        assertThat(target.getStatus()).isEqualTo("200");

        MemberResponseDto data = target.getData();
        Member member = TestUtil.mockMember(1L, "N");
        validAll(data, member);
    }

    @Test
    void updateWithFile() throws IOException {
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header target = memberService.update(mockMemberRequestDto(), 1L ,TestUtil.mockFile());

        assertThat(target.getStatus()).isEqualTo("200");

        MemberResponseDto data = (MemberResponseDto) target.getData();
        Member member = TestUtil.mockMember(1L, "N");
        validNotFile(data, member);
        assertThat(data.getFileOriginName()).isEqualTo("test.txt");

    }

    @Test
    void updateWithoutFile() throws IOException {
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header target = memberService.update(mockMemberRequestDto(), 1L ,null);

        assertThat(target.getStatus()).isEqualTo("200");

        MemberResponseDto data = (MemberResponseDto) target.getData();
        Member member = TestUtil.mockMember(1L, "N");
        validAll(data, member);
    }

    @Test
    void updateNotFoundMember() {
        MemberRequestDto memberRequestDto = mockMemberRequestDto();
        MockMultipartFile mockMultipartFile = TestUtil.mockFile();
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> memberService.update(memberRequestDto, 1L, mockMultipartFile))
                .withMessage("Member Entity가 존재하지 않습니다.");
    }

    @Test
    void delete() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header target = memberService.delete(1L);

        assertThat(target.getStatus()).isEqualTo("200");
    }

    @Test
    void deleteNotFoundMember() {
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> memberService.delete(1L))
                .withMessage("Member Entity가 존재하지 않습니다.");
    }

    @Test
    void findById() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header<MemberResponseDto> target = memberService.findById(1L);

        assertThat(target.getStatus()).isEqualTo("200");

        MemberResponseDto data = target.getData();
        Member member = TestUtil.mockMember(1L, "N");
        validAll(data, member);
    }

    @Test
    void findByIdNotFoundMember () {
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> memberService.findById(1L))
                .withMessage("Member Entity가 존재하지 않습니다.");
    }

    @Test
    void findAll() {
        List<Member> list = new ArrayList<>();
        list.add(TestUtil.mockMember(1L, "Y"));
        list.add(TestUtil.mockMember(2L, "N"));
        list.add(TestUtil.mockMember(3L, "N"));
        list.add(TestUtil.mockMember(4L, "N"));

        given(memberRepository.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(list));

        Header<List<MemberResponseDto>> target = memberService
                .findAll(PageRequest.of(0,4));

        assertThat(target.getStatus()).isEqualTo("200");

        List<MemberResponseDto> memberResponseDtoList = target.getData();
        int i = 0;
        for(MemberResponseDto data : memberResponseDtoList) {
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

    @Test
    void findBySelectYN() {
        given(memberRepository.findBySelectYN("Y"))
                .willReturn(Optional.of(TestUtil.mockMember(1L, "Y")));

        Header<MemberResponseDto> target = memberService.findBySelectYN("Y");

        assertThat(target.getStatus()).isEqualTo("200");

        MemberResponseDto data = target.getData();
        Member member = TestUtil.mockMember(1L, "Y");
        validAll(data, member);
    }

    @Test
    void findBySelectYNNotFoundMember() {
        given(memberRepository.findBySelectYN("Y"))
                .willReturn(Optional.empty());

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> memberService.findBySelectYN("Y"))
                .withMessage("Member Entity가 존재하지 않습니다.");
    }

    @Test
    void totalInfo() {
        given(memberRepository.findMemberWithSkills(1L)).willReturn(Optional.of(TestUtil.mockMember(1L, "N")));

        Header<MemberTotalInfoResponseDto> target = memberService.totalInfo(1L);

        assertThat(target.getStatus()).isEqualTo("200");

        MemberTotalInfoResponseDto data = target.getData();

        MemberResponseDto memberData = data.getMemberResponseDto();
        Member member = TestUtil.mockMember(1L, "N");

        assertAll(
                () -> validAll(memberData, member),
                () -> assertThat(data.getSkillResponseDtoList()).isNullOrEmpty(),
                () -> assertThat(data.getProjectResponseDtoList()).isNullOrEmpty()
        );
    }

    @Test
    void totalInfoNotFoundMember() {
        given(memberRepository.findMemberWithSkills(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> memberService.totalInfo(1L))
                .withMessage("Member Entity가 존재하지 않습니다.");
    }

    @Test
    void updateSelect() {
        Member member = TestUtil.mockMember(1L, "N");
        Member member2 = TestUtil.mockMember(2L, "Y");
        List<Member> list = new ArrayList<>();
        list.add(member);
        list.add(member2);
        list.add(TestUtil.mockMember(3L, "N"));

        given(memberRepository.findBySelectYN("Y")).willReturn(Optional.ofNullable(member2));
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        memberService.updateSelect(1L);
        assertAll(
                () -> assertThat(list.get(0).getSelectYN()).isEqualTo("Y"),
                () -> assertThat(list.get(1).getSelectYN()).isEqualTo("N"),
                () -> assertThat(list.get(2).getSelectYN()).isEqualTo("N")
        );
    }

    @Test
    void updateSelectNotFoundMember() {
        Member member = TestUtil.mockMember(1L, "N");
        Member member2 = TestUtil.mockMember(2L, "Y");
        List<Member> list = new ArrayList<>();
        list.add(member);
        list.add(member2);
        list.add(TestUtil.mockMember(3L, "N"));

        given(memberRepository.findBySelectYN("Y")).willReturn(Optional.ofNullable(member2));
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> memberService.updateSelect(1L))
                .withMessage("Member Entity가 존재하지 않습니다.");
    }

    private void validAll(MemberResponseDto data, Member member) {
        assertAll("memberValidAll",
                () -> assertThat(data.getMemberId()).isEqualTo(member.getId()),
                () -> assertThat(data.getComment()).isEqualTo(member.getComment()),
                () -> assertThat(data.getFileOriginName()).isEqualTo(member.getFileInfo().getFileOriginName()),
                () -> assertThat(data.getFileUrl()).isEqualTo(member.getFileInfo().getFileUrl()),
                () -> assertThat(data.getSubIntroduction()).isEqualTo(member.getSubIntroduction()),
                () -> assertThat(data.getIntroduction()).isEqualTo(member.getIntroduction()),
                () -> assertThat(data.getPhoneNumber()).isEqualTo(member.getPhoneNumber()),
                () -> assertThat(data.getEmail()).isEqualTo(member.getEmail()),
                () -> assertThat(data.getSelectYN()).isEqualTo(member.getSelectYN())
        );
    }

    private void validNotFile(MemberResponseDto data, Member member) {
        assertAll("memberValidNotFile",
                () -> assertThat(data.getMemberId()).isEqualTo(member.getId()),
                () -> assertThat(data.getComment()).isEqualTo(member.getComment()),
                () -> assertThat(data.getSubIntroduction()).isEqualTo(member.getSubIntroduction()),
                () -> assertThat(data.getIntroduction()).isEqualTo(member.getIntroduction()),
                () -> assertThat(data.getPhoneNumber()).isEqualTo(member.getPhoneNumber()),
                () -> assertThat(data.getEmail()).isEqualTo(member.getEmail()),
                () -> assertThat(data.getSelectYN()).isEqualTo(member.getSelectYN())
        );
    }

    private MemberRequestDto mockMemberRequestDto() {
        return MemberRequestDto.builder()
                .comment("comment")
                .subIntroduction("subIntroduction")
                .introduction("introduction")
                .phoneNumber("phoneNumber")
                .email("email")
                .build();
    }
}