package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.AlternateRegistrationStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlternateRegistrationStudentRepository extends JpaRepository<AlternateRegistrationStudent, Long> {
    boolean existsByCode(String code);

    Optional<AlternateRegistrationStudent> findByCode(String code);
}