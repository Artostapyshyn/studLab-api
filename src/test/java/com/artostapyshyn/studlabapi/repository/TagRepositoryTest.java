package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByName() {
        Tag tag = new Tag();
        tag.setName("tag");
        entityManager.persist(tag);
        entityManager.flush();

        Tag result = tagRepository.findByName(tag.getName());
        assertEquals(tag.getName(), result.getName());
    }

    @Test
    void findAllByNameIn() {
        Tag tag1 = new Tag();
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setName("tag2");
        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.flush();
        assertEquals(2, tagRepository.findAllByNameIn(Set.of("tag1","tag2")).size());
    }
}