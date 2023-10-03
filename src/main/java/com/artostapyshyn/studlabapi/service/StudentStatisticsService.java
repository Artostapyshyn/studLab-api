package com.artostapyshyn.studlabapi.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface StudentStatisticsService {

    int countByEnabled(boolean enabled);

    Map<String, Integer> getRegistrationData();

    int getDailyStatistics();

    int getWeeklyStatistics(LocalDateTime startDate, LocalDateTime endDate);

    int getMonthlyStatistics(LocalDateTime startDate, LocalDateTime endDate);

    int getAllTimeStatistics();
}
