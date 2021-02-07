package com.myintroduce.repository.skill;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class SkillRepositoryTest {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void findAllByMember_test() {
        // given
        Member member = givenMember();
        for(int i = 1; i < 6; i++) {
            skillRepository.save(givenSkill(member, i));
        }

        // when
        Page<Skill> list = skillRepository.findAllByMember(member, PageRequest.of(0, 2));

        // then
        assertThat(list.getTotalPages()).isEqualTo(3);
        assertThat(list.getTotalElements()).isEqualTo(5);
        assertThat(list.getNumber()).isEqualTo(0);
        assertThat(list.getNumberOfElements()).isEqualTo(2);
        for(Skill skill : list) {
            assertThat(skill.getMember()).isEqualTo(member);
        }
    }

    public Member givenMember() {
        return memberRepository.save(Member.builder()
                .comment("페이지 탑 영역 내용 부분입니다.")
                .fileInfo(new FileInfo("헤더 이미지 경로","헤더 이미지 원본 이름","파일 주소"))
                .subIntroduction("자기소개 서브 내용 부분입니다.")
                .introduction("자기소개 내용 부분입니다.")
                .phoneNumber("010-1111-1111")
                .email("uok0201@gmail.com")
                .selectYN("Y")
                .build());
    }

    public Skill givenSkill(Member member, int level) {
        return  Skill.builder()
                .skillName("JAVA")
                .fileInfo(new FileInfo("path","java_logo_image","파일주소"))
                .skillLevel(1)
                .level(level)
                .member(member)
                .build();
    }
}