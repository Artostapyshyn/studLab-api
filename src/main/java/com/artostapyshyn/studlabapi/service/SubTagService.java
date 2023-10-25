package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.SubTag;

import java.util.List;
import java.util.Optional;

public interface SubTagService {
    Optional<SubTag> findById(Long id);

    SubTag save(SubTag subTag);

    List<SubTag> findAll();

    void deleteById(Long id);
}
