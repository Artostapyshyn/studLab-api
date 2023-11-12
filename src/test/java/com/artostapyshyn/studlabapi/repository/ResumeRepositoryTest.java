package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Resume;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.apache.commons.lang3.RandomUtils.nextBytes;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class ResumeRepositoryTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByStudentId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Resume resume = new Resume();
        resume.setName("name");
        resume.setStudentId(student.getId());
        resume.setData(nextBytes(10));
        entityManager.persist(resume);
        entityManager.flush();

        List<Resume> found = resumeRepository.findByStudentId(student.getId());
        assertEquals(1, found.size());
        assertEquals(resume, found.iterator().next());
    }
}
