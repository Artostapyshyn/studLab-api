package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Major;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class MajorRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findAll() {
        Major major = new Major();
        major.setName("test");
        testEntityManager.persist(major);
        testEntityManager.flush();
        assertEquals(1, majorRepository.findAll().size());
    }

    @Test
    void findByName() {
        Major major = new Major();
        major.setName("test");
        testEntityManager.persist(major);
        testEntityManager.flush();
        assertEquals("test", majorRepository.findByName("test").getName());
    }
}