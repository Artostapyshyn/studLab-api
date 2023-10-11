package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.SubTag;
import com.artostapyshyn.studlabapi.entity.Tag;
import com.artostapyshyn.studlabapi.repository.TagRepository;
import com.artostapyshyn.studlabapi.service.SubTagService;
import com.artostapyshyn.studlabapi.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private SubTagService subTagService;

    @Override
    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Set<Tag> resolveAndAddTags(Set<Tag> tagsFromEvent) {
        Set<Tag> resolvedTags = new HashSet<>();
        for (Tag tagFromEvent : tagsFromEvent) {
            Tag existingTag = tagRepository.findByName(tagFromEvent.getName());
            if (existingTag != null) {
                resolvedTags.add(existingTag);
                addSubTagsToTag(existingTag);
            } else {
                Tag savedTag = tagRepository.save(tagFromEvent);
                resolvedTags.add(savedTag);
                addSubTagsToTag(savedTag);
            }
        }
        return resolvedTags;
    }

    public void addSubTagsToTag(Tag tag) {
        Set<SubTag> newSubTags = new HashSet<>(tag.getSubTags());
        for (SubTag newSubTag : newSubTags) {
            Optional<SubTag> optionalExistingSubTag = subTagService.findByNameAndTag(newSubTag.getName(), tag);
            if (optionalExistingSubTag.isEmpty()) {
                newSubTag.setTag(tag);
                subTagService.save(newSubTag);
            }
        }
        tagRepository.save(tag);
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }
}
