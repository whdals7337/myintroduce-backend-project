package com.myintroduce.web.api;

import com.myintroduce.repository.skill.SkillRepository;
import com.myintroduce.web.dto.skill.SkillRequestDto;
import com.myintroduce.web.dto.skill.SkillResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/skill")
public class SkillApiController extends CrudWitFileController<SkillRequestDto, SkillResponseDto, SkillRepository> {
}
