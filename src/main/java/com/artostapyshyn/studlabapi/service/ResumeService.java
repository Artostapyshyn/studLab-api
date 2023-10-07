package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Resume;

import java.util.List;
import java.util.Optional;

public interface ResumeService {
    List<Resume> findByStudentId(Long id);

    Optional<Resume> findById(Long id);

    Resume findByName(String name);

    Resume save(Resume resume);

    void deleteById(Long id);

    boolean existsByStudentIdAndName(Long id, String name);
}
