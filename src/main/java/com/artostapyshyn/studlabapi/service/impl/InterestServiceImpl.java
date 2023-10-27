package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.dto.InterestDto;
import com.artostapyshyn.studlabapi.entity.Interest;
import com.artostapyshyn.studlabapi.repository.InterestRepository;
import com.artostapyshyn.studlabapi.service.InterestService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final InterestRepository interestRepository;

    private final ModelMapper modelMapper;

    @Override
    public Optional<Interest> findById(Long id) {
        return interestRepository.findById(id);
    }

    @Override
    public List<InterestDto> findAll() {
        List<Interest> interests = interestRepository.findAll();
        return interests.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    @Override
    public Interest save(Interest interest) {
        return interestRepository.save(interest);
    }

    @Override
    public InterestDto convertToDTO(Interest interest) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper.map(interest, InterestDto.class);
    }
}
