package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.error.exception.skill.SkillNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.skill.SkillRepository;
import com.myintroduce.uploader.Uploader;
import com.myintroduce.utill.FileUtil;
import com.myintroduce.web.dto.skill.SkillRequestDto;
import com.myintroduce.web.dto.skill.SkillResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SkillService extends BaseWithFileService<SkillRequestDto, SkillResponseDto, SkillRepository> {

    private static final String SUFFIX = ".com/";

    @Value("${file.upload-dir}")
    private String fileUploadPath;

    @Value("${file.skill-dir}")
    private String subFileUploadPath;

    private final Uploader uploader;

    private final MemberRepository memberRepository;

    @Override
    public Header<SkillResponseDto> save(SkillRequestDto requestDto, MultipartFile file) throws IOException {
        // [1] member 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        String fileUrl ="";
        try {
            // [2] file upload to S3
            fileUrl = uploader.upload(file, fileUploadPath + "/" + subFileUploadPath);
            FileInfo fileInfo = new FileInfo(FileUtil.cutFileName(file.getOriginalFilename(), 100), fileUrl);

            // [3] project info DB 등록
            Skill skill = baseRepository.save(requestDto.toEntity(fileInfo, member));
            log.info("skill info DB insert" + skill);

            return Header.OK(response(skill));

        } catch (Exception e) {
            log.debug("s3에 저장되었던 skill 파일 삭제");
            uploader.delete(removeSuffixUrl(fileUrl));
            throw e;
        }
    }

    @Override
    public Header<SkillResponseDto> update(SkillRequestDto requestDto, Long id, MultipartFile file) throws IOException {
        Skill skill = baseRepository.findById(id)
                .orElseThrow(SkillNotFoundException::new);

        String fileUrl ="";
        String preExistingFileUrl = skill.getFileInfo().getFileUrl();

        // [1] member 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        // 첨부된 파일이 없는 경우
        if(file == null || file.isEmpty()) {

            // [2] skill info DB update
            skill.update(requestDto.toEntity(skill.getFileInfo(), member));
            log.info("skill info DB update" + skill);

            return Header.OK(response(skill));
        }

        try {
            // [2] file upload to S3
            fileUrl = uploader.upload(file, fileUploadPath + "/" + subFileUploadPath);
            FileInfo fileInfo = new FileInfo(FileUtil.cutFileName(file.getOriginalFilename(), 100), fileUrl);

            // [4] skill info DB update
            skill.update(requestDto.toEntity(fileInfo, member));
            log.info("skill info DB update" + skill);

        } catch (Exception e) {
            log.debug("s3에 저장되었던 project 파일 삭제");
            uploader.delete(removeSuffixUrl(fileUrl));
            throw e;
        }

        // [5] pre-existing file delete
        uploader.delete(removeSuffixUrl(preExistingFileUrl));

        return Header.OK(response(skill));
    }

    @Override
    public Header<SkillResponseDto> delete(Long id) {
        Skill skill = baseRepository.findById(id)
                .orElseThrow(SkillNotFoundException::new);

        // [1] skill info DB delete
        baseRepository.delete(skill);
        log.info("skill info DB delete" + skill);

        // [2] pre-existing file delete
        String preExistingFileUrl = skill.getFileInfo().getFileUrl();
        uploader.delete(removeSuffixUrl(preExistingFileUrl));

        return Header.OK();
    }

    @Override
    @Transactional(readOnly = true)
    public Header<SkillResponseDto> findById(Long id) {
        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseThrow(SkillNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Header<List<SkillResponseDto>> findAll(Pageable pageable) {
        Page<Skill> skills = baseRepository.findAll(pageable);

        List<SkillResponseDto> skillResponseDtoList = skills.stream()
                .map(this::response)
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(skills.getTotalPages())
                .totalElements(skills.getTotalElements())
                .currentPage(skills.getNumber())
                .currentElements(skills.getNumberOfElements())
                .build();

        return Header.OK(skillResponseDtoList, pagination);
    }

    private String removeSuffixUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf(SUFFIX) + SUFFIX.length());
    }

    public SkillResponseDto response(Skill skill) {
        return SkillResponseDto.builder()
                .skillId(skill.getId())
                .skillName(skill.getSkillName())
                .fileUrl(skill.getFileInfo().getFileUrl())
                .fileOriginName(skill.getFileInfo().getFileOriginName())
                .skillLevel(skill.getSkillLevel())
                .level(skill.getLevel())
                .memberId(skill.getMember().getId())
                .build();
    }
}
