package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Complaint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomComplaint;

@ActiveProfiles("test")
@DataJpaTest
class ComplaintRepositoryTest {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByExistingId() {
        Complaint complaint = createRandomComplaint();
        complaint.setStudentId(1L);
        entityManager.persist(complaint);
        entityManager.flush();

        Optional<Complaint> foundComplaint = complaintRepository.findById(complaint.getId());

        Assertions.assertTrue(foundComplaint.isPresent());
        Assertions.assertEquals(complaint.getId(), foundComplaint.get().getId());
    }

    @Test
    void findByIdNonExistingId() {
        Optional<Complaint> foundComplaint = complaintRepository.findById(12345L);

        Assertions.assertTrue(foundComplaint.isEmpty());
    }

    @Test
    void findAll() {
        Complaint complaint1 = createRandomComplaint();
        complaint1.setStudentId(1L);

        Complaint complaint2 = createRandomComplaint();
        complaint2.setStudentId(2L);

        Complaint complaint3 = createRandomComplaint();
        complaint3.setStudentId(3L);

        entityManager.persist(complaint1);
        entityManager.persist(complaint2);
        entityManager.persist(complaint3);
        entityManager.flush();

        List<Complaint> allComplaints = complaintRepository.findAll();

        Assertions.assertEquals(3, allComplaints.size());
        Assertions.assertTrue(allComplaints.contains(complaint1));
        Assertions.assertTrue(allComplaints.contains(complaint2));
        Assertions.assertTrue(allComplaints.contains(complaint3));
    }
}