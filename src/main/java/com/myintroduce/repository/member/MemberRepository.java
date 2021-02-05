package com.myintroduce.repository.member;

import com.myintroduce.domain.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 선택된 멤버 조회
    Optional<Member> findBySelectYN(String selectYN);

    // member & skill fetch 조인 조회
    @Query("select m from Member m left join fetch m.skills where m.id = :id" )
    Optional<Member> findMemberWithSkills(@Param("id") Long id);
}
