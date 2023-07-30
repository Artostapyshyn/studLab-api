package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Student;
import com.artostapyshyn.studlabapi.exception.exceptions.ResourceNotFoundException;
import com.artostapyshyn.studlabapi.service.FileService;
import com.artostapyshyn.studlabapi.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.*;

import static com.artostapyshyn.studlabapi.constant.ControllerConstants.*;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final StudentService studentService;

    private final static String NOT_FOUND = "Student not found";

    @Override
    public ResponseEntity<Map<String, Object>> addDocument(Long studentId, MultipartFile file, Set<String> documents,
                                                           Set<String> documentFilenames, String documentType) throws IOException {
        Map<String, Object> response = new HashMap<>();
        Optional<Student> studentOptional = studentService.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            byte[] documentBytes = file.getBytes();
            String documentBase64 = Base64.getEncoder().encodeToString(documentBytes);

            documents.add(documentBase64);
            studentService.save(student);

            documentFilenames.add(file.getOriginalFilename());
            studentService.save(student);

            response.put(MESSAGE, documentType + " added");
            response.put("fileName", file.getOriginalFilename());
            response.put("documentBase64", documentBase64);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> addResume(Long studentId, MultipartFile file) throws IOException {
        Student student = studentService.findById(studentId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
        return addDocument(studentId, file, student.getResumes(), student.getResumeFilenames(), "Resume");
    }

    @Override
    public ResponseEntity<Map<String, Object>> addCertificate(Long studentId, MultipartFile file) throws IOException {
        Student student = studentService.findById(studentId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
        return addDocument(studentId, file, student.getCertificates(), student.getCertificatesFilenames(), "Certificate");
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteDocument(Long studentId,
                                                              String fileName, Set<String> documents, Set<String> documentFilenames,
                                                              String documentType) {
        boolean removed = false;
        Iterator<String> documentIterator = documents.iterator();
        Iterator<String> filenameIterator = documentFilenames.iterator();
        while (documentIterator.hasNext() && filenameIterator.hasNext()) {
            String document = documentIterator.next();
            String filename = filenameIterator.next();
            if (filename.equals(fileName)) {
                documentIterator.remove();
                filenameIterator.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            Student student = studentService.findById(studentId).orElseThrow(() -> new NotFoundException(NOT_FOUND));
            if (documentType.equals("resume")) {
                student.setResumes(documents);
                student.setResumeFilenames(documentFilenames);
            } else if (documentType.equals("certificate")) {
                student.setCertificates(documents);
                student.setCertificatesFilenames(documentFilenames);
            }
            studentService.save(student);
            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE, documentType + " deleted");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteResume(Long studentId, String fileName) {
        Student student = studentService.findById(studentId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
        return deleteDocument(studentId, fileName, student.getResumes(), student.getResumeFilenames(), "resume");
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteCertificate(Long studentId, String fileName) {
        Student student = studentService.findById(studentId).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
        return deleteDocument(studentId, fileName, student.getCertificates(), student.getCertificatesFilenames(), "certificate");
    }
}
