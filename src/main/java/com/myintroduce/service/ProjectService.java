package com.myintroduce.service;

import com.myintroduce.domain.entity.member.Member;
import com.myintroduce.domain.entity.project.Project;
import com.myintroduce.domain.network.Header;
import com.myintroduce.domain.network.Pagination;
import com.myintroduce.error.exception.member.MemberNotFoundException;
import com.myintroduce.error.exception.project.ProjectNotFoundException;
import com.myintroduce.ifs.crudwithfile.BaseWithFileService;
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
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
    @Transactional
    public Header<ProjectResponseDto> save(ProjectRequestDto requestDto, MultipartFile file) throws IOException {
        log.info("project save start");

        // [1] file parameter setting
        String originalName = FileUtil.cutFileName(Objects.requireNonNull(file.getOriginalFilename()), 100);
        String saveName = FileUtil.getRandomFileName(originalName);
        String fileUrl = domain + "/" + dirType + "/" + subFileUploadPath + "/" + saveName;
        String saveDir = fileUploadPath + subFileUploadPath;
        String savePath =  saveDir +"/"+ saveName;
        log.info("[1] file parameter setting");

        // [2] file 디렉토리 생성
        FileUtil.createDir(saveDir);
        log.info("[2] file 디렉토리 생성");

        // [3] project info DB 등록
        Project project = baseRepository.save(requestDto.toEntity(memberRepository
                .getOne(requestDto.getMemberId()), savePath, originalName, fileUrl));
        log.info("[3] project info DB 등록");

        // [4] file transfer
        file.transferTo(new File(savePath));
        log.info("[4] file transfer");

        log.info("project save end");
        return Header.OK(response(project));
    }

    @Override
    @Transactional
    public Header update(ProjectRequestDto requestDto, Long id, MultipartFile file) {
        log.info("project update start");
        Optional<Project> optional = baseRepository.findById(id);

        return optional.map(project -> {
            // 순서값이 변경 된 경우
            if(project.getLevel() != requestDto.getLevel()){
                int originLevel = project.getLevel();
                int changedLevel = requestDto.getLevel();

                // 원래 순서 값이 변경할 순서 값보다 큰 경우
                if (originLevel > changedLevel) {
                    // 원래 값부터 변경할 순서 값보다 작은 순서의 칼럼의 순서값을 1 증가
                    List<Project> rangeRows = baseRepository.findByLevelBetween(changedLevel, originLevel-1);
                    for(Project row : rangeRows){
                        row.levelUp();
                    }
                }
                // 원래 순서 값이 변경할 순서 값보다 작은 경우
                else {
                    // 원래 값보다 크고 변경할 순서 값보다 작은 순서의 칼럼의 순서값을 1 감소
                    List<Project> rangeRows = baseRepository.findByLevelBetween(originLevel+1, changedLevel);
                    for(Project row : rangeRows){
                        row.levelDown();
                    }
                }
            }
            log.info("[1] 순서 변경");

            // 첨부된 파일이 없는 경우
            if(file == null || file.isEmpty()) {
                log.info("첨부된 파일 없음");

                // [2] project info DB update
                project.update(requestDto.toEntity(memberRepository.getOne(requestDto.getMemberId()),
                        project.getFileInfo().getFilePath(), project.getFileInfo().getFileOriginName(), project.getFileInfo().getFileUrl()));
                log.info("[2] project info DB update");
            }
            // 첨부된 파일이 있는 경우
            else {
                log.info("첨부된 파일 있음");

                // [2] file parameter setting
                String originalName = FileUtil.cutFileName(Objects.requireNonNull(file.getOriginalFilename()), 100);
                String saveName = FileUtil.getRandomFileName(originalName);
                String fileUrl = domain + "/" + dirType + "/" + subFileUploadPath + "/" + saveName;
                String saveDir = fileUploadPath + subFileUploadPath;
                String savePath =  saveDir +"/"+ saveName;
                String preExistingFilePath = project.getFileInfo().getFilePath();
                log.info("[2] file parameter setting");

                // [3] file 디렉토리 생성
                FileUtil.createDir(saveDir);
                log.info("[3] file 디렉토리 생성");

                // [4] project info DB update
                project.update(requestDto.toEntity(memberRepository.getOne(requestDto.getMemberId()),
                        savePath, originalName, fileUrl));
                log.info("[4] project info DB update");

                // [5] file transfer
                try {
                    file.transferTo(new File(savePath));
                } catch (IOException e) {
                    log.info("[5] file transfer fail");
                    e.printStackTrace();
                }
                log.info("[5] file transfer");

                // [6] pre-existing file delete
                FileUtil.deleteFile(preExistingFilePath);
                log.info("[6] pre-existing file delete");
            }

            log.info("project update end");
            return Header.OK(response(project));
        }).orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    @Transactional
    public Header delete(Long id) {
        log.info("project delete start");
        Optional<Project> optional = baseRepository.findById(id);

        return optional.map(project -> {
            // [1] project info DB delete
            baseRepository.delete(project);
            log.info("[1] project info DB delete");

            // [2] pre-existing file delete
            FileUtil.deleteFile(project.getFileInfo().getFilePath());
            log.info("[2] pre-existing file delete");

            log.info("project delete end");
            return Header.OK();
        }).orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    @Transactional
    public Header<ProjectResponseDto> findById(Long id) {
        log.info("project findById start");
        log.info("project findById end");
        return baseRepository.findById(id)
                .map(this::response)
                .map(Header::OK)
                .orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    @Transactional
    public Header<List<ProjectResponseDto>> findAll(ProjectRequestDto requestDto, Pageable pageable) {
        log.info("project findAll start");
        Page<Project> projects;
        // 특정 멤버 id 값이 들어온 경우
        if(requestDto.getMemberId() != null && requestDto.getMemberId() > 0) {
            log.info("exist memberId");
            Member member = memberRepository.findById(requestDto.getMemberId()).orElseThrow(MemberNotFoundException::new);
            projects = baseRepository.findAllByMember(member, pageable);
        }
        else {
            log.info("no memberId");
            projects = baseRepository.findAll(pageable);
        }

        List<ProjectResponseDto> projectResponseDtoList = projects.stream()
                        .map(this::response)
                        .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .totalPages(projects.getTotalPages())
                .totalElements(projects.getTotalElements())
                .currentPage(projects.getNumber())
                .currentElements(projects.getNumberOfElements())
                .build();

        log.info("project findAll end");
        return Header.OK(projectResponseDtoList, pagination);
    }

    @Transactional
    public Project getProject(Long id) {
        return baseRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
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
}
