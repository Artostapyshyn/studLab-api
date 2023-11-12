package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomEvent;

@ActiveProfiles("test")
@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAll() {
        Event event1 = createRandomEvent();
        event1.setNameOfEvent("Event A");

        Event event2 = createRandomEvent();
        event2.setNameOfEvent("Event B");

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.flush();

        List<Event> events = eventRepository.findAll();

        Assertions.assertEquals(2, events.size());
        Assertions.assertEquals("Event A", events.get(0).getNameOfEvent());
        Assertions.assertEquals("Event B", events.get(1).getNameOfEvent());
    }

    @Test
    void findEventById() {
        Event event = createRandomEvent();
        entityManager.persist(event);
        entityManager.flush();

        Optional<Event> result = eventRepository.findEventById(event.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(event.getId(), result.get().getId());
    }
}