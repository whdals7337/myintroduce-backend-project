package com.myintroduce.repository.member;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.repository.skill.SkillRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    public void findBySelectYN_test() {
        // given
        Member expect01 = givenMember("Y");
        Member expect02 = givenMember("N");
        Member expect03 = givenMember("N");
        memberRepository.save(expect01);
        memberRepository.save(expect02);
        memberRepository.save(expect03);

        // when
        Optional<Member> findMember = memberRepository.findBySelectYN("Y");

        //then
        assertThat(findMember.get()).isEqualTo(expect01);
    }

    @Test
    public void findMemberWithSkills_test() {
        // given
        Member expect = givenMember("Y");
        memberRepository.save(expect);

        Skill skill1 = Skill.builder()
                .skillName("skillName")
                .skillLevel(1)
                .level(1)
                .fileInfo(new FileInfo("name","url"))
                .member(expect)
                .build();
        Skill skill2 = Skill.builder()
                .skillName("skillName")
                .skillLevel(1)
                .level(1)
                .fileInfo(new FileInfo("name","url"))
                .member(expect)
                .build();
        skillRepository.save(skill1);
        skillRepository.save(skill2);

        // when
        Member findMember = memberRepository.findMemberWithSkills(expect.getId()).get();

        // then
        assertThat(findMember).isEqualTo(expect);
        assertThat(findMember.getSkills().size()).isEqualTo(2);


    }

    public Member givenMember(String selectYN) {
        return Member.builder()
                .comment("페이지 탑 영역 내용 부분입니다.")
                .fileInfo(new FileInfo("헤더 이미지 원본 이름","파일 주소"))
                .subIntroduction("자기소개 서브 내용 부분입니다.")
                .introduction("자기소개 내용 부분입니다.")
                .phoneNumber("010-1111-1111")
                .email("uok0201@gmail.com")
                .selectYN(selectYN)
                .build();
    }
}