package com.myintroduce.web.api;

import com.myintroduce.domain.network.Header;
import com.myintroduce.ifs.crudwithfile.CrudWitFileController;
import com.myintroduce.repository.member.MemberRepository;
import com.myintroduce.service.MemberService;
import com.myintroduce.web.dto.member.MemberRequestDto;
import com.myintroduce.web.dto.member.MemberResponseDto;
import com.myintroduce.web.dto.membertotalinfo.MemberTotalInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberApiController extends CrudWitFileController<MemberRequestDto, MemberResponseDto, MemberRepository> {

    private final MemberService memberService;

    @GetMapping("/{id}/totalInfo")
    public Header<MemberTotalInfoResponseDto> totalInfo(@PathVariable Long id) {
        return memberService.totalInfo(id);
    }

    @GetMapping("/select")
    public Header<MemberResponseDto> findBySelectYN() {return memberService.findBySelectYN(); }

    @PatchMapping("/select/{id}")
    public Header<MemberResponseDto> updateSelect(@PathVariable Long id) {
        return memberService.updateSelect(id);
    }
}
