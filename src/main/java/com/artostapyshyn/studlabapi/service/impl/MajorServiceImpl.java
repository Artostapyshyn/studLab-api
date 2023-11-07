package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Major;
import com.artostapyshyn.studlabapi.repository.MajorRepository;
import com.artostapyshyn.studlabapi.service.MajorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MajorServiceImpl implements MajorService {

    private MajorRepository majorRepository;

    @Override
    public Major findByName(String name) {
        return majorRepository.findByNameOrderByNameAsc(name);
    }

    @Override
    public Major save(Major major) {
        return majorRepository.save(major);
    }

    @Override
    public List<Major> findAll() {
        return majorRepository.findAll();
    }
}
