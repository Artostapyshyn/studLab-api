package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.AlternateRegistrationStudent;

import java.util.List;
import java.util.Optional;

public interface AlternateRegistrationStudentService {

    List<AlternateRegistrationStudent> findAll();

    Optional<AlternateRegistrationStudent> findById(Long id);

    boolean isValidCode(String code);

    Optional<AlternateRegistrationStudent> findByCode(String code);

    AlternateRegistrationStudent save(AlternateRegistrationStudent alternateRegistrationStudent);

    void delete(AlternateRegistrationStudent alternateRegistrationStudent);
}
