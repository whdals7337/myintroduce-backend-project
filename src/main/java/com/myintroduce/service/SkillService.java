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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SkillService extends BaseWithFileService<SkillRequestDto, SkillResponseDto, SkillRepository> {

    @Value("${file.upload-dir}")
    private String fileUploadPath;

    @Value("${server-domain}")
    private String domain;

    @Value("${file.images-dir}")
    private String dirType;

    @Value("${file.skill-dir}")
    private String subFileUploadPath;

    private final MemberRepository memberRepository;

    @Override
    public Header<SkillResponseDto> save(SkillRequestDto requestDto, MultipartFile file) throws IOException {
        log.info("skill save start");

        // [1] member 조회
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(MemberNotFoundException::new);
        log.info("[1] member 조회");

        // [2] 파일 정보 셋팅
        FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain, dirType, fileUploadPath, subFileUploadPath);
        log.info("[2] 파일 정보 셋팅");

        // [3] project info DB 등록
        Skill skill = baseRepository.save(requestDto.toEntity(fileInfo, member));
        log.info("[3] skill info DB 등록");

        // [4] file transfer
        file.transferTo(new File(fileInfo.getFilePath()));
        log.info("[4] file transfer");

        log.info("project save end");
        return Header.OK(response(skill));
    }

    @Override
    public Header update(SkillRequestDto requestDto, Long id, MultipartFile file) {
        log.info("skill update start");
        Optional<Skill> optional = baseRepository.findById(id);

        return optional.map(skill -> {

            // [1] member 조회
            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElseThrow(MemberNotFoundException::new);
            log.info("[1] member 조회");

            // 첨부된 파일이 없는 경우
            if(file == null || file.isEmpty()) {
                log.info("첨부된 파일 없음");

                // [2] skill info DB update
                skill.update(requestDto.toEntity(skill.getFileInfo(), member));
                log.info("[2] skill info DB update");
            }
            // 첨부된 파일이 있는 경우
            else {
                log.info("첨부된 파일 있음");

                // [3] 파일 정보 셋팅
                FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain, dirType, fileUploadPath, subFileUploadPath);
                String preExistingFilePath = skill.getFileInfo().getFilePath();
                log.info("[3] 파일 정보 셋팅");

                // [4] skill info DB update
                skill.update(requestDto.toEntity(fileInfo, member));
                log.info("[4] skill info DB update");

                // [5] file transfer
                try {
                    file.transferTo(new File(fileInfo.getFilePath()));

                } catch (IOException e) {
                    log.info("[5] file transfer fail");
                    e.printStackTrace();
                }
                log.info("[5] file transfer");

                // [6] pre-existing file delete
                FileUtil.deleteFile(preExistingFilePath);
                log.info("[6] pre-existing file delete");
            }

            log.info("skill update end");

            return Header.OK(response(skill));
        }).orElseThrow(SkillNotFoundException::new);
    }

    @Override
    public Header delete(Long id) {
        log.info("skill delete start");
        Optional<Skill> optional = baseRepository.findById(id);

        return optional.map(skill -> {
            // [1] skill info DB delete
            baseRepository.delete(skill);
            log.info("[1] skill info DB delete");

            // [2] pre-existing file delete
            FileUtil.deleteFile(skill.getFileInfo().getFilePath());
            log.info("[2] pre-existing file delete");

            log.info("member delete end");
            return Header.OK();
        }).orElseThrow(SkillNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Header<SkillResponseDto> findById(Long id) {
        log.info("skill findById start");
        log.info("member findById end");
        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseThrow(SkillNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Header<List<SkillResponseDto>> findAll(Pageable pageable) {
        log.info("skill findAll start");
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

        log.info("member findAll end");
        return Header.OK(skillResponseDtoList, pagination);
    }

    @Transactional(readOnly = true)
    public Skill getSkill(Long id) {
        return baseRepository.findById(id).orElseThrow(SkillNotFoundException::new);
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
