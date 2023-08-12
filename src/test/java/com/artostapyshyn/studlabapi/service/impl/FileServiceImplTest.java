package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomStudent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceImplTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addDocument_shouldAddDocumentAndReturnResponseEntity() throws IOException {
        String documentType = "Resume";
        String fileName = "resume.pdf";
        byte[] documentBytes = "Test Document Content".getBytes();
        String documentBase64 = "VGVzdCBEb2N1bWVudCBDb250ZW50";

        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/pdf", documentBytes);

        Set<String> documents = new HashSet<>();
        Set<String> documentFilenames = new HashSet<>();
        Student student = createRandomStudent();
        student.setId(1L);
        student.setResumes(documents);
        student.setResumeFilenames(documentFilenames);

        when(studentService.findById(1L)).thenReturn(Optional.of(student));
        when(studentService.save(student)).thenReturn(student);

        ResponseEntity<Map<String, Object>> responseEntity = fileService.addDocument(1L, file, documents,
                documentFilenames, documentType);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().containsKey("message"));
        assertEquals(documentType + " added", responseEntity.getBody().get("message"));
        assertTrue(responseEntity.getBody().containsKey("fileName"));
        assertEquals(fileName, responseEntity.getBody().get("fileName"));
        assertTrue(responseEntity.getBody().containsKey("documentBase64"));
        assertEquals(documentBase64, responseEntity.getBody().get("documentBase64"));

        assertTrue(documents.contains(documentBase64));
        assertTrue(documentFilenames.contains(fileName));

        verify(studentService, times(1)).save(student);
    }

    @Test
    void addDocument_shouldReturnNotFoundIfStudentNotFound() throws IOException {
        String documentType = "Resume";
        String fileName = "resume.pdf";
        byte[] documentBytes = "Test Document Content".getBytes();

        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/pdf", documentBytes);

        when(studentService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> responseEntity = fileService.addDocument(1L, file,
                new HashSet<>(), new HashSet<>(), documentType);

        assertEquals(404, responseEntity.getStatusCode().value());
        assertNull(responseEntity.getBody());

        verify(studentService, never()).save(any());
    }

    @Test
    void deleteDocument_shouldDeleteDocumentAndReturnResponseEntity() {
        String documentType = "resume";
        String fileName = "resume.pdf";

        Set<String> documents = new HashSet<>(Collections.singletonList("VGVzdCBEb2N1bWVudCBDb250ZW50"));
        Set<String> documentFilenames = new HashSet<>(Collections.singletonList(fileName));

        Student student = createRandomStudent();
        student.setId(1L);
        student.setResumes(documents);
        student.setResumeFilenames(documentFilenames);

        when(studentService.findById(1L)).thenReturn(Optional.of(student));
        when(studentService.save(student)).thenReturn(student);

        ResponseEntity<Map<String, Object>> responseEntity = fileService.deleteDocument(1L, fileName,
                documents, documentFilenames, documentType);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().containsKey("message"));
        assertEquals(documentType + " deleted", responseEntity.getBody().get("message"));

        assertTrue(student.getResumes().isEmpty());
        assertTrue(student.getResumeFilenames().isEmpty());

        verify(studentService).save(student);
    }

    @Test
    void deleteDocument_shouldReturnNotFoundIfStudentNotFound() {
        String documentType = "resume";
        String fileName = "resume.pdf";

        when(studentService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> responseEntity = fileService.deleteDocument(1L, fileName,
                new HashSet<>(), new HashSet<>(), documentType);

        assertEquals(404, responseEntity.getStatusCode().value());
        assertNull(responseEntity.getBody());

        verify(studentService, never()).save(any());
    }
}
