package com.artostapyshyn.studlabapi.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface FileService {

    ResponseEntity<Map<String, Object>> addDocument(Long studentId, MultipartFile file, Set<String> documents,
                                                    Set<String> documentFilenames, String documentType) throws IOException;

    ResponseEntity<Map<String, Object>> deleteDocument(Long studentId, String fileName,
                                                       Set<String> documents, Set<String> documentFilenames,
                                                       String documentType);

    ResponseEntity<Map<String, Object>> addResume(Long studentId, MultipartFile file) throws IOException;

    ResponseEntity<Map<String, Object>> addCertificate(Long studentId, MultipartFile file) throws IOException;

    ResponseEntity<Map<String, Object>> deleteResume(Long studentId, String fileName);

    ResponseEntity<Map<String, Object>> deleteCertificate(Long studentId, String fileName);
}
