package com.artostapyshyn.studlabapi.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface StudentStatisticsService {

    int countByEnabled(boolean enabled);

    Map<String, Integer> getRegistrationData();

    void updateStatistics(LocalDateTime registrationDate);

    int getDailyStatistics();

    int getWeeklyStatistics();

    int getMonthlyStatistics();

    int getAllTimeStatistics();
}
