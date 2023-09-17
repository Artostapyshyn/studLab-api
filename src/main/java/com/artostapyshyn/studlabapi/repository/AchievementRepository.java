package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByStudentId(Long studentId);
}