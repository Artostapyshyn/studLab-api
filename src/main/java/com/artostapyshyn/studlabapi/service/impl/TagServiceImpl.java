package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.TagDto;
import com.artostapyshyn.studlabapi.entity.Tag;
import com.artostapyshyn.studlabapi.repository.TagRepository;
import com.artostapyshyn.studlabapi.service.TagService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final ModelMapper modelMapper;

    @Override
    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    @Transactional
    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    @Override
    public Set<Tag> resolveAndAddTags(Set<Tag> tagsFromEvent) {
        Set<Tag> resolvedTags = new HashSet<>();
        for (Tag tagFromEvent : tagsFromEvent) {
            Tag existingTag = tagRepository.findByName(tagFromEvent.getName());
            if (existingTag != null) {
                resolvedTags.add(existingTag);
            } else {
                Tag savedTag = tagRepository.save(tagFromEvent);
                resolvedTags.add(savedTag);
            }
        }
        return resolvedTags;
    }


    @Override
    public List<TagDto> findAll() {
        return tagRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public TagDto convertToDto(Tag tag) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper.map(tag, TagDto.class);
    }
}
