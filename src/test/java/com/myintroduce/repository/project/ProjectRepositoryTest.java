package com.myintroduce.repository.project;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByLevelBetween_test() {
        // given
        Member member = givenMember();
        for(int i = 1; i < 6; i++) {
            projectRepository.save(givenProject(member, i));
        }

        // when
        List<Project> list = projectRepository.findByLevelBetween(2,5);

        // then
        assertThat(list.size()).isEqualTo(4);
        for(Project project : list) {
            assertThat(project.getLevel()).isGreaterThanOrEqualTo(2);
            assertThat(project.getLevel()).isLessThanOrEqualTo(5);
        }
    }

    @Test
    void findAllByMember_test() {
        // given
        Member member = givenMember();
        for(int i = 1; i < 6; i++) {
            projectRepository.save(givenProject(member, i));
        }

        // when
        Page<Project> list = projectRepository.findAllByMember(member, PageRequest.of(0, 2));

        // then
        assertThat(list.getTotalPages()).isEqualTo(3);
        assertThat(list.getTotalElements()).isEqualTo(5);
        assertThat(list.getNumber()).isZero();
        assertThat(list.getNumberOfElements()).isEqualTo(2);
        for(Project project : list) {
            assertThat(project.getMember()).isEqualTo(member);
        }
    }

    public Member givenMember() {
        return memberRepository.save(Member.builder()
                .comment("페이지 탑 영역 내용 부분입니다.")
                .fileInfo(new FileInfo("헤더 이미지 원본 이름","파일 주소"))
                .subIntroduction("자기소개 서브 내용 부분입니다.")
                .introduction("자기소개 내용 부분입니다.")
                .phoneNumber("010-1111-1111")
                .email("uok0201@gmail.com")
                .selectYN("Y")
                .build());
    }

    public Project givenProject(Member member , int level) {
        return  Project.builder()
                .projectTitle("프로젝트 이름")
                .projectContent("프로젝트는 Spring4 + angularJs를 기반으로 개발된 프로젝트입니다.")
                .projectPostScript("#Spring #angularJs #현장실습")
                .fileInfo(new FileInfo("이름","주소"))
                .projectLink("http://aergaerg")
                .level(level)
                .member(member)
                .build();
    }
}