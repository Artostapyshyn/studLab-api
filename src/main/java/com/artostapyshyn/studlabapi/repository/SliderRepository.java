package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Slider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {
    Optional<Slider> findById(Long id);
}