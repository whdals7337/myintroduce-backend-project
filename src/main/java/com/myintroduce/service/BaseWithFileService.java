package com.myintroduce.service;

import com.myintroduce.ifs.crudwithfile.CrudWithFileInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseWithFileService<Req, Res, Repository> implements CrudWithFileInterface<Req, Res> {

    @Autowired(required = false)
    protected Repository baseRepository;
}
