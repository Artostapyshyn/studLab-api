package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByStudentId(Long id);

    Resume findByName(String name);
}