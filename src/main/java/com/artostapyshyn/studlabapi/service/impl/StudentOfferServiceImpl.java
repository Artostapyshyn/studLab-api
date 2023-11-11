package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.StudentOfferEditDto;
import com.artostapyshyn.studlabapi.dto.StudentServiceOfferDto;
import com.artostapyshyn.studlabapi.entity.StudentServiceOffer;
import com.artostapyshyn.studlabapi.repository.StudentServiceOfferRepository;
import com.artostapyshyn.studlabapi.service.StudentOfferService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentOfferServiceImpl implements StudentOfferService {

    private final StudentServiceOfferRepository studentOfferRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<StudentServiceOfferDto> findAll() {
        List<StudentServiceOffer> services = studentOfferRepository.findAll();
        return services.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public StudentServiceOffer save(StudentServiceOffer studentServiceOffer) {
        return studentOfferRepository.save(studentServiceOffer);
    }

    @Override
    public StudentServiceOfferDto convertToDTO(StudentServiceOffer studentServiceOffer) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper.map(studentServiceOffer, StudentServiceOfferDto.class);
    }

    @Override
    public List<StudentServiceOfferDto> findAllByProviderId(Long id) {
        List<StudentServiceOffer> services = studentOfferRepository.findAllByProviderId(id);
        return services.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void updateStudentServices(StudentServiceOffer existingService, StudentOfferEditDto updatedService) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(updatedService, existingService);
        studentOfferRepository.save(existingService);
    }

    @Override
    public Optional<StudentServiceOffer> findById(Long id) {
        return studentOfferRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        studentOfferRepository.deleteById(id);
    }
}
