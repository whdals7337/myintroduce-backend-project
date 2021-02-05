package com.myintroduce.domain.entity.member;

import com.myintroduce.domain.BaseTimeEntity;
import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.skill.Skill;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@ToString(exclude = {"projectList", "skillList"})
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String comment;
    private String subIntroduction;
    private String introduction;
    private String phoneNumber;
    private String email;
    private String selectYN;

    @Embedded
    private FileInfo fileInfo;

    @OneToMany(mappedBy = "member")
    private List<Project> projectList;

    @OneToMany(mappedBy = "member")
    private List<Skill> skillList;

    public void update(Member member){
        this.comment = member.getComment();
        this.subIntroduction = member.getSubIntroduction();
        this.introduction =member.getIntroduction();
        this.phoneNumber = member.getPhoneNumber();
        this.email = member.getEmail();
        this.selectYN = member.getSelectYN();
        this.fileInfo = member.getFileInfo();
    }

    public void select() {
        this.selectYN ="Y";
    }

    public void unSelect() {
        this.selectYN ="N";
    }
}
