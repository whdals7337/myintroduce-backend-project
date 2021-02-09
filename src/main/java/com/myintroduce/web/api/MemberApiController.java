package com.myintroduce.web.api;

import com.myintroduce.domain.network.Header;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.service.MemberService;
import com.myintroduce.web.dto.member.MemberRequestDto;
import com.myintroduce.web.dto.member.MemberResponseDto;
import com.myintroduce.web.dto.membertotalinfo.MemberTotalInfoResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberApiController extends CrudWitFileController<MemberRequestDto, MemberResponseDto, MemberRepository> {

    private final MemberService memberService;

    @ApiOperation(value = "멤버의 전체 조회", notes = "id값을 통해서 멤버의 프로필, 프로젝트, 스킬 정보 전체를 조회합니다.")
    @GetMapping("/{id}/totalInfo")
    public Header<MemberTotalInfoResponseDto> totalInfo(@PathVariable Long id) {
        return memberService.totalInfo(id);
    }

    @ApiOperation(value = "선택된 멤버 정보 조회", notes = "selectYN값이 Y인 멤버의 정보를 조회합니다.")
    @GetMapping("/select")
    public Header<MemberResponseDto> findBySelectYN() {return memberService.findBySelectYN(); }

    @ApiOperation(value = "멤버 선택", notes = "id값을 통해서 멤버의 selectYN값을 Y값으로 수정합니다.")
    @PatchMapping("/select/{id}")
    public Header<MemberResponseDto> updateSelect(@PathVariable Long id) {
        return memberService.updateSelect(id);
    }
}
