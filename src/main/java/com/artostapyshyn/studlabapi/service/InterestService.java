package com.artostapyshyn.studlabapi.service;

import com.artostapyshyn.studlabapi.dto.InterestDto;
import com.artostapyshyn.studlabapi.entity.Interest;

import java.util.List;
import java.util.Optional;

public interface InterestService {
    Optional<Interest> findById(Long id);

    List<InterestDto> findAll();

    Interest save(Interest interest);

    InterestDto convertToDTO(Interest interest);
}
