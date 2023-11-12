package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Friendship;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DataJpaTest
class FriendshipRepositoryTest {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByStudentId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Student student1 = createRandomStudent();
        entityManager.persist(student1);
        entityManager.flush();

        assertNotNull(friendshipRepository.findAllByStudentId(student.getId()));
        Friendship friendShip = new Friendship();
        friendShip.setStudent(student);
        friendShip.setFriend(student1);
        entityManager.persist(friendShip);
        entityManager.flush();
        assertNotNull(friendshipRepository.findAllByStudentId(student.getId()));

    }

    @Test
    void findFriendshipByStudentId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Friendship friendShip = new Friendship();
        friendShip.setStudent(student);
        entityManager.persist(friendShip);
        entityManager.flush();
        assertNotNull(friendshipRepository.findFriendshipByStudentId(student.getId()));
    }

    @Test
    void deleteByFriendId() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();

        Student student1 = createRandomStudent();
        entityManager.persist(student1);
        entityManager.flush();

        Friendship friendShip = new Friendship();
        friendShip.setStudent(student);
        friendShip.setFriend(student1);
        entityManager.persist(friendShip);
        entityManager.flush();
        friendshipRepository.deleteByFriendId(student1.getId());
        assertNotNull(friendshipRepository.findAllByStudentId(student.getId()));
    }
}