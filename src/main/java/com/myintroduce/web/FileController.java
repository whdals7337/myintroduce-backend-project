package com.myintroduce.web;

import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.error.exception.project.ProjectNotFoundException;
import com.myintroduce.error.exception.skill.SkillNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.project.ProjectRepository;
import com.myintroduce.repository.skill.SkillRepository;
import com.myintroduce.uploader.Uploader;
import com.myintroduce.utill.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class FileController {

    private final MemberRepository memberRepository;

    private final ProjectRepository projectRepository;

    private final SkillRepository skillRepository;

    private final Uploader uploader;

    @GetMapping("/download/{type}/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable String type,
                                                 @PathVariable("id") Long id,
                                                 HttpServletRequest request) throws IOException {
        String S3key = null;
        String filename = null;

        switch (type) {
            case "member" :
                Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
                S3key =  member.getFileInfo().S3key();
                filename =  member.getFileInfo().getFileOriginName();
                break;
            case "project" :
                Project project = projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
                S3key = project.getFileInfo().S3key();
                filename = project.getFileInfo().getFileOriginName();
                break;
            case "skill" :
                Skill skill = skillRepository.findById(id).orElseThrow(SkillNotFoundException::new);
                S3key = skill.getFileInfo().S3key();
                filename = skill.getFileInfo().getFileOriginName();
                break;
            default:
                throw new FileNotFoundException("존재하지않는 파일입니다.");
        }

        filename = FileUtil.getFileNameByBrowser(filename, request);
        Resource resource = uploader.downloadResource(S3key);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
