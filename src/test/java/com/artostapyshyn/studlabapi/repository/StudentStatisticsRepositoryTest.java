package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.StudentStatistics;
import com.artostapyshyn.studlabapi.enums.Interval;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
class StudentStatisticsRepositoryTest {

    @Autowired
    private StudentStatisticsRepository studentStatisticsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testGetDailyStatistics() {
        StudentStatistics statistics = new StudentStatistics();
        statistics.setInterval(Interval.DAY);
        statistics.setCount(100);
        entityManager.persist(statistics);

        int dailyStatistics = studentStatisticsRepository.getDailyStatistics();
        assertEquals(100, dailyStatistics);
    }

    @Test
    void testGetWeeklyStatistics() {
        StudentStatistics statistics = new StudentStatistics();
        statistics.setInterval(Interval.WEEK);
        statistics.setCount(500);
        entityManager.persist(statistics);

        int weeklyStatistics = studentStatisticsRepository.getWeeklyStatistics();
        assertEquals(500, weeklyStatistics);
    }

    @Test
    void testGetMonthlyStatistics() {
        StudentStatistics statistics = new StudentStatistics();
        statistics.setInterval(Interval.MONTH);
        statistics.setCount(2000);
        entityManager.persist(statistics);

        int monthlyStatistics = studentStatisticsRepository.getMonthlyStatistics();
        assertEquals(2000, monthlyStatistics);
    }

    @Test
    void testGetAllTimeStatistics() {
        StudentStatistics statistics = new StudentStatistics();
        statistics.setInterval(Interval.ALL_TIME);
        statistics.setCount(10000);
        entityManager.persist(statistics);

        int allTimeStatistics = studentStatisticsRepository.getAllTimeStatistics();
        assertEquals(10000, allTimeStatistics);
    }

    @Test
    void testFindByInterval() {
        StudentStatistics statistics = new StudentStatistics();
        statistics.setInterval(Interval.DAY);
        statistics.setCount(100);
        entityManager.persist(statistics);

        StudentStatistics result = studentStatisticsRepository.findByInterval(Interval.DAY);
        assertEquals(statistics, result);
    }
}

