package com.myintroduce.web;

import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.service.MemberService;
import com.myintroduce.service.ProjectService;
import com.myintroduce.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

@RequiredArgsConstructor
@RestController
public class FileController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final SkillService skillService;

    @GetMapping("/download/{type}/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable String type, @PathVariable("id") Long id, HttpServletRequest request) throws IOException {
        String filePath;
        String filename;

        switch (type) {
            case "member" :
                Member member = memberService.getMember(id);
                filePath =  member.getFileInfo().getFilePath();
                filename =  member.getFileInfo().getFileOriginName();
                break;
            case "project" :
                Project project = projectService.getProject(id);
                filePath = project.getFileInfo().getFilePath();
                filename = project.getFileInfo().getFileOriginName();
                break;
            case "skill" :
                Skill skill = skillService.getSkill(id);
                filePath = skill.getFileInfo().getFilePath();
                filename = skill.getFileInfo().getFileOriginName();
                break;
            default:
                filePath = null;
                filename = null;
        }

        if(filePath != null && filename != null){
            filename = getFileNameByBrowser(filename, request);
            Path path = Paths.get(filePath);
            Resource resource = new InputStreamResource(Files.newInputStream(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        }
        else {
            throw new FileNotFoundException("존재하지않는 파일입니다.");
        }
    }

    protected String getFileNameByBrowser(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        String browser= "";
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if(headerName.equals("user-agent")) {
                browser = request.getHeader(headerName);
            }
        }

        String docName = "";
        if (browser.contains("Trident") || browser.contains("MSIE") || browser.contains("Edge")) {
            docName = mappingSpecialCharacter(URLEncoder.encode(fileName, "UTF-8"));

        } else if (browser.contains("Firefox")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else if (browser.contains("Opera")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else if (browser.contains("Chrome")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        } else if (browser.contains("Safari")) {
            docName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        }
        return docName;
    }

    public static String mappingSpecialCharacter(String name) {

        // 파일명에 사용되는 특수문자
        char[] sh_list = { '~', '!', '@', '#', '$', '%', '&', '(', ')', '=', ';', '[', ']', '{', '}', '^', '-' };
        try {
            for (char sh : sh_list) {
                String encodeStr = URLEncoder.encode(sh + "", "UTF-8");
                name = name.replaceAll(encodeStr, "\\" + sh);
            }

            // 띄워쓰기 -> + 치환
            name = name.replaceAll("%2B", "+");
            // 콤마 -> _ 치환
            name = name.replaceAll("%2C", "_");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }
}
