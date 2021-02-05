package com.myintroduce.web.api;


import com.myintroduce.domain.network.Header;
import com.myintroduce.error.exception.file.FileNotRequestException;
import com.myintroduce.ifs.crudwithfile.CrudWithFileInterface;
import com.myintroduce.service.BaseWithFileService;
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
    @PostMapping("")
    public Header<Res> save(Req requestDto, @RequestParam(name="file") MultipartFile file) throws IOException {
        if(file == null || file.isEmpty()){
            throw new FileNotRequestException();
        }
        return baseService.save(requestDto, file);
    }

    @Override
    @PutMapping("{id}")
    public Header<Res> update(Req requestDto, @PathVariable Long id, @RequestParam(name="file", required=false) MultipartFile file) throws IOException {
        return baseService.update(requestDto, id, file);
    }

    @Override
    @DeleteMapping("{id}")
    public Header delete( @PathVariable Long id) {
        return baseService.delete(id);
    }

    @Override
    @GetMapping("{id}")
    public Header<Res> findById( @PathVariable Long id) {
        return baseService.findById(id);
    }

    @Override
    @GetMapping("")
    public Header<List<Res>> findAll(Req requestDto, @PageableDefault(sort="rgDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return baseService.findAll(requestDto, pageable);
    }
}

