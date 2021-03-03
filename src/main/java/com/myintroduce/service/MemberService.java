package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.entity.skill.Skill;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.file.FileNotTransferException;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.utill.FileUtil;
import com.myintroduce.web.dto.member.MemberRequestDto;
import com.myintroduce.web.dto.member.MemberResponseDto;
import com.myintroduce.web.dto.membertotalinfo.MemberTotalInfoResponseDto;
import com.myintroduce.web.dto.project.ProjectResponseDto;
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
public class MemberService extends BaseWithFileService<MemberRequestDto, MemberResponseDto, MemberRepository> {

    @Value("${file.upload-dir}")
    private String fileUploadPath;
    @Value("${server-domain}")
    private String domain;
    @Value("${file.images-dir}")
    private String dirType;
    @Value("${file.member-dir}")
    private String subFileUploadPath;

    private final SkillService skillService;

    private final ProjectService projectService;

    @Override
    public Header<MemberResponseDto> save(MemberRequestDto requestDto, MultipartFile file) throws IOException {
        log.info("member save start");

        // [1] member 생성 및 파일 정보 셋팅
        FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain, dirType, fileUploadPath, subFileUploadPath);
        log.info("[1] member 생성 및 파일 정보 셋팅");

        // [2] member info DB 등록
        Member member = baseRepository.save(requestDto.toEntity(fileInfo, "N"));
        log.info("[2] member info DB 등록");

        // [3] file transfer
        file.transferTo(new File(fileInfo.getFilePath()));
        log.info("[3] file transfer");

        log.info("member save end");
        return Header.OK(response(member));
    }

    @Override
    public Header update(MemberRequestDto requestDto, Long id, MultipartFile file) {
        log.info("member update start");
        Optional<Member> optional = baseRepository.findById(id);

        return optional.map(member -> {
            // 첨부된 파일이 없는 경우
            if(file == null || file.isEmpty()) {
                log.info("첨부된 파일 없음");

                // [1] member info DB update
                member.update(requestDto.toEntity(member.getFileInfo(), member.getSelectYN()));
                log.info("[1] member info DB update");
            }
            // 첨부된 파일이 있는 경우
            else {
                log.info("첨부된 파일 있음");

                // [1] member 생성 및 파일 정보 셋팅
                FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain, dirType, fileUploadPath, subFileUploadPath);
                String preExistingFilePath = member.getFileInfo().getFilePath();
                log.info("[1] member 생성 및 파일 정보 셋팅");

                // [2] member info DB update
                member.update(requestDto.toEntity(fileInfo, member.getSelectYN()));
                log.info("[2] member info DB update");

                // [4\3] file transfer
                try {
                    file.transferTo(new File(fileInfo.getFilePath()));
                } catch (IOException e) {
                    log.info("[3] file transfer fail");
                    e.printStackTrace();
                }
                log.info("[3] file transfer");


                // [4] pre-existing file delete
                FileUtil.deleteFile(preExistingFilePath);
                log.info("[4] pre-existing file delete");
            }

            log.info("member update end");
            return Header.OK(response(member));
        }).orElseThrow(MemberNotFoundException::new);
    }

    @Override
    public Header delete(Long id) {
        log.info("member delete start");
        Optional<Member> optional = baseRepository.findById(id);

        return optional.map(member -> {
            // [1] member info DB delete
            baseRepository.delete(member);
            log.info("[1] member info DB delete");

            // [2] pre-existing file delete
            FileUtil.deleteFile(member.getFileInfo().getFilePath());
            log.info("[2] pre-existing file delete");

            log.info("member delete end");
            return Header.OK();

        }).orElseThrow(MemberNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Header<MemberResponseDto> findById(Long id) {
        log.info("member findById start");
        log.info("member findById end");
        return baseRepository.findById(id)
                .map((this::response))
                .map(Header::OK)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Header<List<MemberResponseDto>> findAll(Pageable pageable) {
        log.info("member findAll start");
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

        log.info("member findAll end");
        return Header.OK(memberResponseDtoList, pagination);
    }

    @Transactional(readOnly = true)
    public Header<MemberResponseDto> findBySelectYN() {
        log.info("member findBySelectYN start");
        log.info("member findBySelectYN end");
        return baseRepository.findBySelectYN("Y")
                .map(this::response)
                .map(Header::OK)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Header<MemberTotalInfoResponseDto> totalInfo(Long id) {
        log.info("member totalInfo start");

        // [1] MemberResponseDto 조회
        Member member = baseRepository.findMemberWithSkills(id).orElseThrow(MemberNotFoundException::new);
        MemberResponseDto memberResponseDto = response(member);
        log.info("[1] MemberResponseDto 조회");

        // [2] skillResponseDtoList 조회
        List<Skill> skillList = member.getSkills();
        List<SkillResponseDto> skillResponseDtoList = null;
        if(skillList != null) {
            skillResponseDtoList = skillList.stream()
                    .map(skillService::response)
                    .map(response -> Header.OK(response).getData())
                    .collect(Collectors.toList());
            log.info("[2] skillResponseDtoList 조회");
        }

        // [3] projectResponseDtoList 조회
        List<Project> projectList = member.getProjects();
        List<ProjectResponseDto> projectResponseDtoList = null;
        if(projectList != null) {
            projectResponseDtoList = projectList.stream()
                    .map(projectService::response)
                    .collect(Collectors.toList());
            log.info("[3] projectResponseDtoList 조회");
        }

        // [4] MemberTotalInfoResponseDto SET
        MemberTotalInfoResponseDto memberTotalInfoResponseDto = MemberTotalInfoResponseDto.builder()
                .memberResponseDto(memberResponseDto)
                .skillResponseDtoList(skillResponseDtoList)
                .projectResponseDtoList(projectResponseDtoList)
                .build();
        log.info("[4] MemberTotalInfoResponseDto SET");

        log.info("member totalInfo end");
        return Header.OK(memberTotalInfoResponseDto);
    }

    public Header<MemberResponseDto> updateSelect(Long id){
        List<Member> memberList = baseRepository.findAll();
        for(Member member : memberList){
            member.unSelect();
        }
        Member member = baseRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        member.select();

        return Header.OK(response(member));
    }

    public Member getMember(Long id) {
        return baseRepository.findById(id).orElseThrow(MemberNotFoundException::new);
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
