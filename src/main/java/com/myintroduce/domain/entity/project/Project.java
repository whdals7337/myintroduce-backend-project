package com.myintroduce.domain.entity.project;

import com.myintroduce.domain.BaseTimeEntity;
import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = {"member"})
public class Project extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String projectTitle;
    private String projectContent;
    private String projectPostScript;
    private String projectLink;
    private int level;

    @Embedded
    private FileInfo fileInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Project(Long id, String projectTitle, String projectContent, String projectPostScript,
                   String projectLink, int level, FileInfo fileInfo, Member member) {
        this.id = id;
        this.projectTitle = projectTitle;
        this.projectContent = projectContent;
        this.projectPostScript = projectPostScript;
        this.projectLink = projectLink;
        this.level = level;
        this.fileInfo = fileInfo;
        if(member != null) {
            changeProfile(member);
        }
    }

    public void update(Project project) {
        this.projectTitle = project.getProjectTitle();
        this.projectContent = project.getProjectContent();
        this.projectPostScript = project.getProjectPostScript();
        this.fileInfo = project.getFileInfo();
        this.projectLink = project.getProjectLink();
        this.level = project.getLevel();

        if(project.getMember() != null) {
            changeProfile(project.getMember());
        }
    }

    // 프로젝트와 연결된 프로필 변경
    public void changeProfile(Member member) {
        this.member = member;
        member.getProjects().add(this);
    }

    public void levelUp() {
        this.level += 1;
    }

    public void levelDown() {
        this.level -= 1;
    }
}
