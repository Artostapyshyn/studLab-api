package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Achievement;
import com.artostapyshyn.studlabapi.repository.AchievementRepository;
import com.artostapyshyn.studlabapi.service.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public List<Achievement> findByStudentId(Long id) {
        return achievementRepository.findByStudentId(id);
    }

    @Override
    public Optional<Achievement> findById(Long id) {
        return achievementRepository.findById(id);
    }

    @Override
    public Achievement save(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public void deleteById(Long id) {
        achievementRepository.deleteById(id);
    }
}
