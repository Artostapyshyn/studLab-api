package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.StudentStatistics;
import com.artostapyshyn.studlabapi.enums.Interval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentStatisticsRepository extends JpaRepository<StudentStatistics, Long> {
    @Query("SELECT SUM(s.count) FROM StudentStatistics s WHERE s.interval = 'DAY'")
    int getDailyStatistics();

    @Query("SELECT SUM(s.count) FROM StudentStatistics s WHERE s.interval = 'WEEK'")
    int getWeeklyStatistics();

    @Query("SELECT SUM(s.count) FROM StudentStatistics s WHERE s.interval = 'MONTH'")
    int getMonthlyStatistics();

    @Query("SELECT SUM(s.count) FROM StudentStatistics s WHERE s.interval = 'ALL_TIME'")
    int getAllTimeStatistics();

    StudentStatistics findByInterval(Interval interval);
}