package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Complaint;
import com.artostapyshyn.studlabapi.repository.ComplaintRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class ComplaintServiceImplTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    @Test
    void delete() {
        Complaint complaint = createRandomComplaint();

        complaintService.delete(complaint);
        verify(complaintRepository, times(1)).delete(complaint);
    }

    @Test
    void save() {
        Complaint complaint = createRandomComplaint();

        when(complaintRepository.save(complaint)).thenReturn(complaint);

        Complaint savedComplaint = complaintService.save(complaint);

        assertNotNull(savedComplaint);
        assertEquals(complaint, savedComplaint);
        verify(complaintRepository, times(1)).save(complaint);
    }

    @Test
    void findById() {
        Long complaintId = 123L;
        Complaint complaint = createRandomComplaint();

        when(complaintRepository.findById(complaintId)).thenReturn(Optional.of(complaint));

        Optional<Complaint> foundComplaint = complaintService.findById(complaintId);

        assertTrue(foundComplaint.isPresent());
        assertEquals(complaint, foundComplaint.get());
    }

    @Test
    void findAll() {
        List<Complaint> expectedComplaints = List.of(
                createRandomComplaint(),
                createRandomComplaint(),
                createRandomComplaint()
        );
        when(complaintRepository.findAll()).thenReturn(expectedComplaints);

        List<Complaint> actualComplaints = complaintService.findAll();

        assertEquals(expectedComplaints.size(), actualComplaints.size());
        assertTrue(actualComplaints.containsAll(expectedComplaints));
    }

    @Test
    void findClosedComplaints() {
        List<Complaint> expectedClosedComplaints = List.of(
                createRandomComplaint(),
                createRandomComplaint()
        );
        when(complaintRepository.findClosedComplaints()).thenReturn(expectedClosedComplaints);

        List<Complaint> actualClosedComplaints = complaintService.findClosedComplaints();

        assertEquals(expectedClosedComplaints.size(), actualClosedComplaints.size());
        assertTrue(actualClosedComplaints.containsAll(expectedClosedComplaints));
    }

}