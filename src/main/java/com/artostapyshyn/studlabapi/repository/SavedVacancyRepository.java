package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.SavedVacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedVacancyRepository extends JpaRepository<SavedVacancy, Long> {
    List<SavedVacancy> findByStudentId(Long studentId);

    SavedVacancy findByStudentIdAndVacancyId(Long studentId, Long vacancyId);
}