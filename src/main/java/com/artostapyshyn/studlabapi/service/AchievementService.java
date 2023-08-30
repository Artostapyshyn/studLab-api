package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Achievement;

import java.util.List;
import java.util.Optional;

public interface AchievementService {
    List<Achievement> findByStudentId(Long id);

    Optional<Achievement> findById(Long id);

    Achievement save(Achievement achievement);

    void deleteById(Long id);
}
