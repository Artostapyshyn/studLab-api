package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.TagDto;
import com.artostapyshyn.studlabapi.entity.Tag;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface TagService {
    Optional<Tag> findById(Long id);

    Tag save(Tag tag);

    List<TagDto> findAll();

    Set<Tag> resolveAndAddTags(Set<Tag> tagsFromEvent);

    void deleteById(Long id);

    TagDto convertToDto(Tag tag);
}
