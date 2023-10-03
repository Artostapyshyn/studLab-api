package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.repository.StudentStatisticsRepository;
import com.artostapyshyn.studlabapi.service.StudentStatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class StudentStatisticServiceImpl implements StudentStatisticsService {

    private final StudentRepository studentRepository;

    private final StudentStatisticsRepository studentStatisticsRepository;

    @Override
    public int countByEnabled(boolean enabled) {
        LocalDateTime date = LocalDateTime.of(2023, 8, 1, 0, 0);
        return studentRepository.countByEnabled(true, date);
    }

    public Map<String, Integer> getRegistrationData() {
        Map<String, Integer> registrationData = new HashMap<>();

        int dayStatistics = getDailyStatistics();
        registrationData.put("Day", dayStatistics);

        int weekStatistics = getWeeklyStatistics(LocalDateTime.now(), LocalDateTime.now().minusDays(7));
        registrationData.put("Week", weekStatistics);

        int monthStatistics = getMonthlyStatistics(LocalDateTime.now(), LocalDateTime.now().minusMonths(1));
        registrationData.put("Month", monthStatistics);

        int totalStatistics = getAllTimeStatistics();
        registrationData.put("Total", totalStatistics);

        return registrationData;
    }


    @Override
    public int getDailyStatistics() {
        return studentStatisticsRepository.getDailyStatistics();
    }

    @Override
    public int getWeeklyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return studentStatisticsRepository.getWeeklyStatistics(startDate, endDate);
    }

    @Override
    public int getMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return studentStatisticsRepository.getMonthlyStatistics(startDate, endDate);
    }

    @Override
    public int getAllTimeStatistics() {
        return studentStatisticsRepository.getAllTimeStatistics();
    }
}
