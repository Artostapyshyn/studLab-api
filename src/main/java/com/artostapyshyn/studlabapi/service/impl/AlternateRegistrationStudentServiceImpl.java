package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.AlternateRegistrationStudent;
import com.artostapyshyn.studlabapi.repository.AlternateRegistrationStudentRepository;
import com.artostapyshyn.studlabapi.service.AlternateRegistrationStudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AlternateRegistrationStudentServiceImpl implements AlternateRegistrationStudentService {

    private final AlternateRegistrationStudentRepository alternateRegistrationStudentRepository;

    @Override
    public List<AlternateRegistrationStudent> findAll() {
        return alternateRegistrationStudentRepository.findAll();
    }

    @Override
    public Optional<AlternateRegistrationStudent> findById(Long id) {
        return alternateRegistrationStudentRepository.findById(id);
    }

    @Override
    public AlternateRegistrationStudent save(AlternateRegistrationStudent alternateRegistrationStudent) {
        return alternateRegistrationStudentRepository.save(alternateRegistrationStudent);
    }

    @Override
    public void delete(AlternateRegistrationStudent alternateRegistrationStudent) {
        alternateRegistrationStudentRepository.delete(alternateRegistrationStudent);
    }

    @Override
    public boolean isValidCode(String code) {
        return alternateRegistrationStudentRepository.existsByCode(code);
    }

    @Override
    public Optional<AlternateRegistrationStudent> findByCode(String code) {
        return alternateRegistrationStudentRepository.findByCode(code);
    }
}
