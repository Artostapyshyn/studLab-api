package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.StudentStatistics;
import com.artostapyshyn.studlabapi.entity.UpdateDates;
import com.artostapyshyn.studlabapi.enums.Interval;
import com.artostapyshyn.studlabapi.repository.StudentRepository;
import com.artostapyshyn.studlabapi.repository.StudentStatisticsRepository;
import com.artostapyshyn.studlabapi.repository.UpdateDatesRepository;
import com.artostapyshyn.studlabapi.service.StudentStatisticsService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class StudentStatisticServiceImpl implements StudentStatisticsService {

    private final StudentRepository studentRepository;

    private final UpdateDatesRepository updateDatesRepository;

    private final StudentStatisticsRepository studentStatisticsRepository;

    @Override
    public int countByEnabled(boolean enabled) {
        return studentRepository.countByEnabled(true);
    }

    @Cacheable(value = "registrationData")
    public Map<String, Integer> getRegistrationData() {
        Map<String, Integer> registrationData = new HashMap<>();

        int dayStatistics = getDailyStatistics();
        registrationData.put("Day", dayStatistics);

        int weekStatistics = getWeeklyStatistics();
        registrationData.put("Week", weekStatistics);

        int monthStatistics = getMonthlyStatistics();
        registrationData.put("Month", monthStatistics);

        int totalStatistics = getAllTimeStatistics();
        registrationData.put("Total", totalStatistics);

        return registrationData;
    }

    @Override
    @CacheEvict(value = {"registrationData", "dailyStatistics", "weeklyStatistics", "monthlyStatistics", "allTimeStatistics"}, allEntries = true)
    public void updateStatistics(LocalDateTime registrationDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate();
        YearMonth currentMonth = YearMonth.from(now);

        UpdateDates updateDates = updateDatesRepository.findById(1L).orElse(new UpdateDates());

        if (updateDates.getLastUpdateDate() == null || !currentDate.equals(updateDates.getLastUpdateDate())) {
            updateStatisticsForInterval(Interval.DAY);
            updateDates.setLastUpdateDate(currentDate);

            if (updateDates.getLastUpdateWeek() == null || currentDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                updateStatisticsForInterval(Interval.WEEK);
                updateDates.setLastUpdateWeek(currentDate);
            }

            if (updateDates.getLastUpdateMonth() == null || !currentMonth.equals(updateDates.getLastUpdateMonth())) {
                updateStatisticsForInterval(Interval.MONTH);
                updateDates.setLastUpdateMonth(currentMonth);
            }

            updateStatisticsForInterval(Interval.ALL_TIME);

            updateDatesRepository.save(updateDates);
        }
    }

    private void updateStatisticsForInterval(Interval interval) {
        StudentStatistics studentStatistics = studentStatisticsRepository.findByInterval(interval);

        if (studentStatistics == null) {
            studentStatistics = new StudentStatistics();
            studentStatistics.setInterval(interval);
            studentStatistics.setCount(1);
        } else {
            studentStatistics.setCount(studentStatistics.getCount() + 1);
        }

        studentStatisticsRepository.save(studentStatistics);
    }

    @Cacheable(value = "dailyStatistics")
    @Override
    public int getDailyStatistics() {
        return studentStatisticsRepository.getDailyStatistics();
    }

    @Cacheable(value = "weeklyStatistics")
    @Override
    public int getWeeklyStatistics() {
        return studentStatisticsRepository.getWeeklyStatistics();
    }

    @Cacheable(value = "monthlyStatistics")
    @Override
    public int getMonthlyStatistics() {
        return studentStatisticsRepository.getMonthlyStatistics();
    }

    @Cacheable(value = "allTimeStatistics")
    @Override
    public int getAllTimeStatistics() {
        return studentStatisticsRepository.getAllTimeStatistics();
    }
}
