package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.entity.Slider;

import java.util.List;
import java.util.Optional;

public interface SliderService {
    Optional<Slider> findById(Long id);

    List<Slider> findAll();

    Slider save(Slider slider);

    void deleteById(Long id);
}
