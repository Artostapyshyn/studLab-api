package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    Optional<Tag> findById(Long id);

    Tag save(Tag tag);

    List<Tag> findAll();

    void deleteById(Long id);
}
