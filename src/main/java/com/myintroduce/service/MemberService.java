package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.uploader.Uploader;
import com.myintroduce.utill.FileUtil;
import com.myintroduce.web.dto.member.MemberRequestDto;
import com.myintroduce.web.dto.member.MemberResponseDto;
import com.myintroduce.web.dto.membertotalinfo.MemberTotalInfoResponseDto;
import com.myintroduce.web.dto.project.ProjectResponseDto;
import com.myintroduce.web.dto.skill.SkillResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MemberService extends BaseWithFileService<MemberRequestDto, MemberResponseDto, MemberRepository> {

    private static final String SUFFIX = ".com/";

    @Value("${file.upload-dir}")
    private String fileUploadPath;

    @Value("${file.member-dir}")
    private String subFileUploadPath;

    private final Uploader uploader;

    private final SkillService skillService;

    private final ProjectService projectService;

    @Override
    public Header<MemberResponseDto> save(MemberRequestDto requestDto, MultipartFile file) throws IOException {
        String fileUrl = "";
        try {
            // [1] file upload to S3
            fileUrl = uploader.upload(file, fileUploadPath + "/" + subFileUploadPath);
            FileInfo fileInfo = new FileInfo(FileUtil.cutFileName(file.getOriginalFilename(), 100), fileUrl);

            // [2] member info DB 등록
            Member member = baseRepository.save(requestDto.toEntity(fileInfo, "N"));
            log.info("member info DB insert" + member);

            return Header.OK(response(member));

        } catch (Exception e) {
            log.debug("s3에 저장되었던 member 파일 삭제");
            uploader.delete(removeSuffixUrl(fileUrl));
            throw e;
        }
    }

    @Override
    public Header<MemberResponseDto> update(MemberRequestDto requestDto, Long id, MultipartFile file) throws IOException {
        Member member = baseRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        String fileUrl ="";
        String preExistingFileUrl = member.getFileInfo().getFileUrl();

        if (file == null || file.isEmpty()) {
            // [1] member info DB update
            member.update(requestDto.toEntity(member.getFileInfo(), member.getSelectYN()));
            log.info("member info DB update" + member);

            return Header.OK(response(member));
        }

        try {
            // [1] file upload to S3
            fileUrl = uploader.upload(file, fileUploadPath + "/" + subFileUploadPath);
            FileInfo fileInfo = new FileInfo(FileUtil.cutFileName(file.getOriginalFilename(), 100), fileUrl);

            // [2] member info DB update
            member.update(requestDto.toEntity(fileInfo, member.getSelectYN()));
            log.info("member info DB update" + member);

        } catch (Exception e) {
            log.debug("s3에 저장되었던 member 파일 삭제");
            uploader.delete(removeSuffixUrl(fileUrl));
            throw e;
        }

        // [3] pre-existing file delete
        uploader.delete(removeSuffixUrl(preExistingFileUrl));

        return Header.OK(response(member));
    }

    @Override
    public Header<MemberResponseDto> delete(Long id) {
        Member member = baseRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        // [1] member info DB delete
        baseRepository.delete(member);
        log.info("member info DB delete" + member);

        // [2] pre-existing file delete
        String preExistingFileUrl = member.getFileInfo().getFileUrl();
        uploader.delete(removeSuffixUrl(preExistingFileUrl));

        return Header.OK();
    }

    @Override
    @Transactional(readOnly = true)
    public Header<MemberResponseDto> findById(Long id) {
        return baseRepository.findById(id)
                .map((this::response))
                .map(Header::OK)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Header<List<MemberResponseDto>> findAll(Pageable pageable) {
        Page<Member> members = baseRepository.findAll(pageable);

        List<MemberResponseDto> memberResponseDtoList = members.stream()
                .map(this::response)
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(members.getTotalPages())
                .totalElements(members.getTotalElements())
                .currentPage(members.getNumber())
                .currentElements(members.getNumberOfElements())
                .build();

        return Header.OK(memberResponseDtoList, pagination);
    }

    @Transactional(readOnly = true)
    @Cacheable(key="#flag", value="findBySelectYN")
    public Header<MemberResponseDto> findBySelectYN(String flag) {
        return baseRepository.findBySelectYN(flag)
                .map(this::response)
                .map(Header::OK)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Header<MemberTotalInfoResponseDto> totalInfo(Long id) {
        // [1] Member & Skill fetch 조인을 통한 조회
        Member member = baseRepository.findMemberWithSkills(id)
                .orElseThrow(MemberNotFoundException::new);
        MemberResponseDto memberResponseDto = response(member);

        // [2] skillResponseDtoList 조회
        List<Skill> skillList = member.getSkills();
        List<SkillResponseDto> skillResponseDtoList = null;
        if(skillList != null && !skillList.isEmpty()) {
            skillResponseDtoList = skillList.stream()
                    .map(skillService::response)
                    .map(res -> Header.OK(res).getData())
                    .collect(Collectors.toList());
        }

        // [3] projectResponseDtoList 조회
        List<Project> projectList = member.getProjects();
        List<ProjectResponseDto> projectResponseDtoList = null;
        if(projectList != null && !projectList.isEmpty()) {
            projectResponseDtoList = projectList.stream()
                    .map(projectService::response)
                    .collect(Collectors.toList());
        }

        // [4] MemberTotalInfoResponseDto SET
        MemberTotalInfoResponseDto memberTotalInfoResponseDto = MemberTotalInfoResponseDto.builder()
                .memberResponseDto(memberResponseDto)
                .skillResponseDtoList(skillResponseDtoList)
                .projectResponseDtoList(projectResponseDtoList)
                .build();

        return Header.OK(memberTotalInfoResponseDto);
    }

    public Header<MemberResponseDto> updateSelect(Long id){
        Optional<Member> selectedMember = baseRepository.findBySelectYN("Y");
        selectedMember.ifPresent(Member::unSelect);

        Member member = baseRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        member.select();

        return Header.OK(response(member));
    }

    private String removeSuffixUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf(SUFFIX) + SUFFIX.length());
    }

    private MemberResponseDto response(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getId())
                .comment(member.getComment())
                .fileUrl(member.getFileInfo().getFileUrl())
                .fileOriginName(member.getFileInfo().getFileOriginName())
                .subIntroduction(member.getSubIntroduction())
                .introduction(member.getIntroduction())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .selectYN(member.getSelectYN())
                .build();
    }
}
