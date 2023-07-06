package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.University;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomUniversity;


@ActiveProfiles("test")
@DataJpaTest
class UniversityRepositoryTest {

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findById() {
        University university = createRandomUniversity();
        entityManager.persist(university);
        entityManager.flush();

        Optional<University> result = universityRepository.findById(university.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(university.getId(), result.get().getId());
    }

    @Test
    void findByDomain() {
        University university = createRandomUniversity();
        entityManager.persist(university);
        entityManager.flush();

        University result = universityRepository.findByDomain(university.getDomain());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(university.getDomain(), result.getDomain());
    }

    @Test
    void findAll() {
        University university1 = createRandomUniversity();
        University university2 = createRandomUniversity();
        entityManager.persist(university1);
        entityManager.persist(university2);
        entityManager.flush();

        List<University> universities = universityRepository.findAll();

        Assertions.assertEquals(2, universities.size());
        Assertions.assertTrue(universities.contains(university1));
        Assertions.assertTrue(universities.contains(university2));
    }
}