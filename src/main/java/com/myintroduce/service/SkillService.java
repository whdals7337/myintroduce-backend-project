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

import java.util.List;
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
    public Header<SkillResponseDto> save(SkillRequestDto requestDto, MultipartFile file) {
        // [1] member 조회
        Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(MemberNotFoundException::new);

        // [2] 파일 정보 셋팅
        FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain,
                dirType, fileUploadPath, subFileUploadPath);

        // [3] project info DB 등록
        Skill skill = baseRepository.save(requestDto.toEntity(fileInfo, member));
        log.info("skill info DB insert" + skill);

        // [4] file transfer
        FileUtil.transferFile(file, fileInfo.getFilePath());

        return Header.OK(response(skill));
    }

    @Override
    public Header update(SkillRequestDto requestDto, Long id, MultipartFile file) {
        Optional<Skill> optional = baseRepository.findById(id);

        return optional.map(skill -> {

            // [1] member 조회
            Member member = memberRepository.findById(requestDto.getMemberId())
                    .orElseThrow(MemberNotFoundException::new);

            // 첨부된 파일이 없는 경우
            if(file == null || file.isEmpty()) {

                // [2] skill info DB update
                skill.update(requestDto.toEntity(skill.getFileInfo(), member));
                log.info("skill info DB update" + skill);
            }
            // 첨부된 파일이 있는 경우
            else {
                // [3] 파일 정보 셋팅
                FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain,
                        dirType, fileUploadPath, subFileUploadPath);
                String preExistingFilePath = skill.getFileInfo().getFilePath();

                // [4] skill info DB update
                skill.update(requestDto.toEntity(fileInfo, member));
                log.info("skill info DB update" + skill);

                // [5] file transfer
                FileUtil.transferFile(file, fileInfo.getFilePath());

                // [6] pre-existing file delete
                FileUtil.deleteFile(preExistingFilePath);
            }

            return Header.OK(response(skill));
        }).orElseThrow(SkillNotFoundException::new);
    }

    @Override
    public Header delete(Long id) {
        Optional<Skill> optional = baseRepository.findById(id);

        return optional.map(skill -> {
            // [1] skill info DB delete
            baseRepository.delete(skill);
            log.info("skill info DB delete" + skill);

            // [2] pre-existing file delete
            FileUtil.deleteFile(skill.getFileInfo().getFilePath());

            return Header.OK();
        }).orElseThrow(SkillNotFoundException::new);
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
