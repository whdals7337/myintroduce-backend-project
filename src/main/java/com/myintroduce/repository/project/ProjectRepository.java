package com.myintroduce.repository.project;

import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 프로젝트 순서 값 범위 조회
    List<Project> findByLevelBetween(int preLevel, int lastLevel);

    // 특정 멤버의 프로젝트 목록 조회
    Page<Project> findAllByMember(Member member, Pageable pageable);
}
