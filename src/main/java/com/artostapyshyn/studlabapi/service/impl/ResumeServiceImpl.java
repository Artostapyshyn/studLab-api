package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Resume;
import com.artostapyshyn.studlabapi.repository.ResumeRepository;
import com.artostapyshyn.studlabapi.service.ResumeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    @Override
    public List<Resume> findByStudentId(Long id) {
        return resumeRepository.findByStudentId(id);
    }

    @Override
    public Optional<Resume> findById(Long id) {
        return resumeRepository.findById(id);
    }

    @Override
    public Resume findByName(String name) {
        return resumeRepository.findByName(name);
    }

    @Override
    public Resume save(Resume resume) {
        return resumeRepository.save(resume);
    }

    @Override
    public void deleteById(Long id) {
        resumeRepository.deleteById(id);
    }

    @Override
    public boolean existsByStudentIdAndName(Long id, String name) {
        return resumeRepository.existsByStudentIdAndName(id, name);
    }
}
