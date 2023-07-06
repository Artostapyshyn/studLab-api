package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Event;
import com.artostapyshyn.studlabapi.entity.FavouriteEvent;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.artostapyshyn.studlabapi.util.TestUtils.*;

@ActiveProfiles("test")
@DataJpaTest
class FavouriteEventRepositoryTest {

    @Autowired
    private FavouriteEventRepository favouriteEventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByStudentId() {
        Event event = createRandomEvent();
        entityManager.persist(event);

        Student student = createRandomStudent();
        entityManager.persist(student);

        FavouriteEvent favouriteEvent1 = createRandomFavouriteEvent(event);
        favouriteEvent1.setStudentId(student.getId());
        entityManager.persist(favouriteEvent1);

        FavouriteEvent favouriteEvent2 = createRandomFavouriteEvent(event);
        favouriteEvent2.setStudentId(student.getId());
        entityManager.persist(favouriteEvent2);
        entityManager.flush();

        List<FavouriteEvent> result = favouriteEventRepository.findByStudentId(student.getId());

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(student.getId(), result.get(0).getStudentId());
        Assertions.assertEquals(student.getId(), result.get(1).getStudentId());
    }

    @Test
    void findByStudentIdAndEventId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Event event = createRandomEvent();
        entityManager.persist(event);
        entityManager.flush();

        FavouriteEvent favouriteEvent1 = createRandomFavouriteEvent(event);
        favouriteEvent1.setStudentId(student.getId());
        favouriteEvent1.setEvent(event);
        entityManager.persist(favouriteEvent1);

        entityManager.flush();

        FavouriteEvent result = favouriteEventRepository.findByStudentIdAndEventId(student.getId(), event.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(student.getId(), result.getStudentId());
        Assertions.assertEquals(event, result.getEvent());
    }
}