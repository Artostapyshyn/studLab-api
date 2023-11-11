package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.StudentOfferEditDto;
import com.artostapyshyn.studlabapi.dto.StudentServiceOfferDto;
import com.artostapyshyn.studlabapi.entity.StudentServiceOffer;

import java.util.List;
import java.util.Optional;

public interface StudentOfferService {

    List<StudentServiceOfferDto> findAll();

    StudentServiceOffer save(StudentServiceOffer studentServiceOffer);

    StudentServiceOfferDto convertToDTO(StudentServiceOffer studentServiceOffer);

    List<StudentServiceOfferDto> findAllByProviderId(Long id);

    void updateStudentServices(StudentServiceOffer existingService, StudentOfferEditDto updatedService);

    Optional<StudentServiceOffer> findById(Long id);

    void deleteById(Long id);
}
