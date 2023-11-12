package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.FriendRequest;
import com.artostapyshyn.studlabapi.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.enums.RequestStatus.PENDING;
import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static io.jsonwebtoken.lang.Assert.notNull;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class FriendRequestRepositoryTest {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Student persistStudent() {
        Student student = createRandomStudent();
        entityManager.persist(student);
        entityManager.flush();
        return student;
    }

    @Test
    void findAllByReceiverId() {
        Student sender = persistStudent();
        Student receiver = persistStudent();

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        entityManager.persist(friendRequest);
        entityManager.flush();

        List<FriendRequest> ids = friendRequestRepository.findAllByReceiverId(receiver.getId());
        assertEquals(1, ids.size());
        assertNotNull(ids);
    }

    @Test
    void findAllByReceiverIdAndStatus() {
        Student sender = persistStudent();
        Student receiver = persistStudent();

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(PENDING);
        entityManager.persist(friendRequest);
        entityManager.flush();

        List<FriendRequest> ids = friendRequestRepository.findAllByReceiverIdAndStatus(receiver.getId(), PENDING);
        assertEquals(1, ids.size());
        assertNotNull(ids);
    }


    @Test
    void findAllByStatusIn() {
        Student sender = persistStudent();
        Student receiver = persistStudent();

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(PENDING);
        entityManager.persist(friendRequest);
        entityManager.flush();

        List<FriendRequest> ids = friendRequestRepository.findAllByStatusIn(List.of(PENDING));
        assertEquals(1, ids.size());
        notNull(ids);

    }

    @Test
    void findBySenderIdAndReceiverId() {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderIdAndReceiverId(1L, 2L);
        assertNotNull(friendRequest);
    }
}