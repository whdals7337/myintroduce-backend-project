package com.myintroduce.domain.entity.skill;

import com.myintroduce.domain.BaseTimeEntity;
import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = {"member"})
public class Skill extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    private String skillName;
    private Integer skillLevel;
    private int level;

    @Embedded
    private FileInfo fileInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Skill(String skillName, Integer skillLevel, int level,
                 FileInfo fileInfo, Member member) {
        this.skillName = skillName;
        this.skillLevel = skillLevel;
        this.level = level;
        this.fileInfo = fileInfo;
        if(member != null) {
            changeProfile(member);
        }
    }

    public void update(Skill skill) {
        this.skillName = skill.getSkillName();
        this.skillLevel = skill.getSkillLevel();
        this.level= skill.getLevel();
        this.fileInfo = skill.getFileInfo();
        if(skill.getMember() != null) {
            changeProfile(skill.getMember());
        }
    }

    public void changeProfile(Member member) {
        this.member = member;
        member.getSkills().add(this);
    }

    public void levelUp() {
        this.level += 1;
    }

    public void levelDown() {
        this.level -=1;
    }
}
