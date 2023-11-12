package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Achievement;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.apache.commons.lang3.RandomUtils.nextBytes;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class AchievementRepositoryTest {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByStudentId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();
        Achievement achievement = new Achievement();
        achievement.setStudentId(student.getId());
        achievement.setName("Test");
        achievement.setData(nextBytes(10));
        entityManager.flush();
        entityManager.persist(achievement);

        Achievement achievement1 = new Achievement();
        achievement1.setStudentId(student.getId());
        achievement1.setData(nextBytes(10));
        achievement1.setName("Test1");
        entityManager.flush();
        entityManager.persist(achievement1);

        Achievement achievement2 = new Achievement();
        achievement2.setStudentId(student.getId());
        achievement2.setName("Test2");
        achievement2.setData(nextBytes(10));
        entityManager.flush();
        entityManager.persist(achievement2);

        Iterable<Achievement> achievements = achievementRepository.findByStudentId(student.getId());

        assertEquals(3, achievements.spliterator().getExactSizeIfKnown());
    }
}