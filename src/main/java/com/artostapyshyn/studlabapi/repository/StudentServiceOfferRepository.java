package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.StudentServiceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentServiceOfferRepository extends JpaRepository<StudentServiceOffer, Long> {
    List<StudentServiceOffer> findAllByProviderId(Long id);
}