package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
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

    @Test
    void findPopularEvents() {
        Event event1 = createRandomEvent();
        event1.setFavoriteCount(5);

        Event event2 = createRandomEvent();
        event2.setFavoriteCount(10);

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.flush();

        List<Event> popularEvents = eventRepository.findPopularEvents();

        Assertions.assertEquals(2, popularEvents.size());
        Assertions.assertEquals(event2.getFavoriteCount(), popularEvents.get(0).getFavoriteCount());
        Assertions.assertEquals(event1.getFavoriteCount(), popularEvents.get(1).getFavoriteCount());
    }

    @Test
    void findAllEventsByDateDesc() {
        Event event1 = createRandomEvent();
        event1.setDate(LocalDateTime.of(2023, 7, 1, 12, 0));

        Event event2 = createRandomEvent();
        event2.setDate(LocalDateTime.of(2023, 7, 2, 12, 0));

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.flush();

        List<Event> eventsByDateDesc = eventRepository.findAllEventsByDateDesc();

        Assertions.assertEquals(2, eventsByDateDesc.size());
        Assertions.assertEquals(event2.getDate(), eventsByDateDesc.get(0).getDate());
        Assertions.assertEquals(event1.getDate(), eventsByDateDesc.get(1).getDate());
    }

    @Test
    void findAllEventsByCreationDateDesc() {
        Event event1 = createRandomEvent();
        event1.setCreationDate(LocalDateTime.of(2023, 7, 1, 12, 0));

        Event event2 = createRandomEvent();
        event2.setCreationDate(LocalDateTime.of(2023, 7, 2, 12, 0));

        entityManager.persist(event1);
        entityManager.persist(event2);
        entityManager.flush();

        List<Event> eventsByCreationDateDesc = eventRepository.findAllEventsByCreationDateDesc();

        Assertions.assertEquals(2, eventsByCreationDateDesc.size());
        Assertions.assertEquals(event2.getCreationDate(), eventsByCreationDateDesc.get(0).getCreationDate());
        Assertions.assertEquals(event1.getCreationDate(), eventsByCreationDateDesc.get(1).getCreationDate());
    }

}