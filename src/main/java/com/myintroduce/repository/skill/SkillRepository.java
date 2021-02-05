package com.myintroduce.repository.skill;

import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    // 특정 멤버의 스킬 목록 조회
    Page<Skill> findAllByMember(Member member, Pageable pageable);
}
