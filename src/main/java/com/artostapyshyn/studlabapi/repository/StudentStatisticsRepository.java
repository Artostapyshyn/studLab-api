package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.StudentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StudentStatisticsRepository extends JpaRepository<StudentStatistics, Long> {
    @Query("SELECT COUNT(*) FROM Student s WHERE s.enabled = true AND s.registrationDate = CURRENT_DATE")
    int getDailyStatistics();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enabled = true AND s.registrationDate BETWEEN :startDate AND :endDate")
    int getWeeklyStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.enabled = true AND s.registrationDate BETWEEN :startDate AND :endDate")
    int getMonthlyStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(*) FROM Student s WHERE s.enabled = true AND s.registrationDate > '2023-08-01'")
    int getAllTimeStatistics();
}