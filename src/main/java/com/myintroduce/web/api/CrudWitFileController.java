package com.myintroduce.web.api;


import com.myintroduce.domain.network.Header;
import com.myintroduce.error.exception.file.FileNotRequestException;
import com.myintroduce.ifs.crudwithfile.CrudWithFileInterface;
import com.myintroduce.service.BaseWithFileService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public abstract class CrudWitFileController<Req, Res, Repository> implements CrudWithFileInterface<Req, Res> {

    @Autowired(required = false)
    protected BaseWithFileService<Req, Res, Repository> baseService;

    @Override
    @ApiOperation(value = "정보 저장", notes = "정보를 저장합니다.")
    @PostMapping("")
    public Header<Res> save(Req requestDto, @RequestParam(name="file") MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new FileNotRequestException();
        }
        return baseService.save(requestDto, file);
    }

    @Override
    @ApiOperation(value = "정보 수정", notes = "id값을 이용하여 정보를 수정합니다.")
    @PutMapping("{id}")
    public Header<Res> update(Req requestDto, @PathVariable Long id, @RequestParam(name="file", required=false) MultipartFile file) throws IOException {
        return baseService.update(requestDto, id, file);
    }

    @Override
    @ApiOperation(value = "정보 삭제", notes = "id값을 이용하여 정보를 삭제합니다.")
    @DeleteMapping("{id}")
    public Header delete( @PathVariable Long id) {
        return baseService.delete(id);
    }

    @Override
    @ApiOperation(value = "정보 조회", notes = "id값을 이용하여 정보를 조회합니다.")
    @GetMapping("{id}")
    public Header<Res> findById( @PathVariable Long id) {
        return baseService.findById(id);
    }

    @Override
    @ApiOperation(value = "전체 정보 조회", notes = "전체 정보를 조회합니다.")
    @GetMapping("")
    public Header<List<Res>> findAll(@PageableDefault(sort="createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return baseService.findAll(pageable);
    }
}

