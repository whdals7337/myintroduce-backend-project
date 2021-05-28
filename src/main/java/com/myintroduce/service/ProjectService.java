package com.myintroduce.service;

import com.myintroduce.domain.FileInfo;
import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.error.exception.project.ProjectNotFoundException;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.repository.project.ProjectRepository;
import com.myintroduce.utill.FileUtil;
import com.myintroduce.web.dto.project.ProjectRequestDto;
import com.myintroduce.web.dto.project.ProjectResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProjectService extends BaseWithFileService<ProjectRequestDto, ProjectResponseDto, ProjectRepository> {

    @Value("${file.upload-dir}")
    private String fileUploadPath;
    @Value("${server-domain}")
    private String domain;
    @Value("${file.images-dir}")
    private String dirType;
    @Value("${file.project-dir}")
    private String subFileUploadPath;

    private final MemberRepository memberRepository;

    @Override
    public Header<ProjectResponseDto> save(ProjectRequestDto requestDto, MultipartFile file) {
        // [1] member 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        // [2] file parameter setting
        FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain,
                dirType, fileUploadPath, subFileUploadPath);

        // [3] project info DB 등록
        Project project = baseRepository.save(requestDto.toEntity(fileInfo, member));
        log.info("project info DB insert" + project);

        // [4] file transfer
        FileUtil.transferFile(file, fileInfo.getFilePath());

        return Header.OK(response(project));
    }

    @Override
    public Header update(ProjectRequestDto requestDto, Long id, MultipartFile file) {
        Project project = baseRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        // [1] 순서 변경
        changeLevel(project.getLevel(), requestDto.getLevel());

        // [2] member 조회
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        if(file == null || file.isEmpty()) {
            // [3] project info DB update
            project.update(requestDto.toEntity(project.getFileInfo(), member));
            log.info("project info DB update" + project);

            return Header.OK(response(project));
        }

        // [3] 파일 정보 셋팅
        FileInfo fileInfo = FileUtil.getFileInfo(file.getOriginalFilename(), domain,
                dirType, fileUploadPath, subFileUploadPath);
        String preExistingFilePath = project.getFileInfo().getFilePath();

        // [4] project info DB update
        project.update(requestDto.toEntity(fileInfo, member));
        log.info("project info DB update" + project);

        // [5] file transfer
        FileUtil.transferFile(file, fileInfo.getFilePath());

        // [6] pre-existing file delete
        FileUtil.deleteFile(preExistingFilePath);

        return Header.OK(response(project));
    }

    @Override
    public Header delete(Long id) {
        Project project = baseRepository.findById(id)
                .orElseThrow(ProjectNotFoundException::new);

        // [1] project info DB delete
        baseRepository.delete(project);
        log.info("project info DB delete" + project);

        // [2] pre-existing file delete
        FileUtil.deleteFile(project.getFileInfo().getFilePath());

        return Header.OK();
    }

    @Override
    @Transactional(readOnly = true)
    public Header<ProjectResponseDto> findById(Long id) {
        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Header<List<ProjectResponseDto>> findAll(Pageable pageable) {
        Page<Project> projects = baseRepository.findAll(pageable);

        List<ProjectResponseDto> projectResponseDtoList = projects.stream()
                        .map(this::response)
                        .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(projects.getTotalPages())
                .totalElements(projects.getTotalElements())
                .currentPage(projects.getNumber())
                .currentElements(projects.getNumberOfElements())
                .build();

        return Header.OK(projectResponseDtoList, pagination);
    }

    public ProjectResponseDto response(Project project) {
        return ProjectResponseDto.builder()
                .projectId(project.getId())
                .projectTitle(project.getProjectTitle())
                .projectContent(project.getProjectContent())
                .projectPostScript(project.getProjectPostScript())
                .fileOriginName(project.getFileInfo().getFileOriginName())
                .fileUrl(project.getFileInfo().getFileUrl())
                .projectLink(project.getProjectLink())
                .level(project.getLevel())
                .memberId(project.getMember().getId())
                .build();
    }

    private void changeLevel(int originLevel, int changedLevel) {
        // 순서변경이 없는 경우
        if(originLevel == changedLevel) return;

        if (originLevel > changedLevel) {
            levelUp(changedLevel, originLevel-1);
            return;
        }
        // 원래 순서 값이 변경할 순서 값보다 작은 경우
        levelDown(originLevel+1, changedLevel);
    }

    private void levelUp(int leftLevel, int rightLevel) {
        // 원래 값부터 변경할 순서 값보다 작은 순서의 칼럼의 순서값을 1 증가
        List<Project> rangeRows = baseRepository.findByLevelBetween(leftLevel, rightLevel);
        for(Project row : rangeRows) {
            row.levelUp();
        }
    }

    private void levelDown(int leftLevel, int rightLevel) {
        // 원래 값보다 크고 변경할 순서 값보다 작은 순서의 칼럼의 순서값을 1 감소
        List<Project> rangeRows = baseRepository.findByLevelBetween(leftLevel, rightLevel);
        for(Project row : rangeRows) {
            row.levelDown();
        }
    }
}
