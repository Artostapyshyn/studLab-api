package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.SubTag;
import com.artostapyshyn.studlabapi.entity.Tag;
import com.artostapyshyn.studlabapi.repository.SubTagRepository;
import com.artostapyshyn.studlabapi.service.SubTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubTagServiceImpl implements SubTagService {

    private final SubTagRepository subTagRepository;

    @Override
    public Optional<SubTag> findById(Long id) {
        return subTagRepository.findById(id);
    }

    @Override
    public SubTag save(SubTag subTag) {
        return subTagRepository.save(subTag);
    }

    @Override
    public List<SubTag> findAll() {
        return subTagRepository.findAll();
    }

    @Override
    public Optional<SubTag> findByNameAndTag(String name, Tag tag) {
        return subTagRepository.findByNameAndTag(name, tag);
    }

    @Override
    public void deleteById(Long id) {
        subTagRepository.deleteById(id);
    }
}
