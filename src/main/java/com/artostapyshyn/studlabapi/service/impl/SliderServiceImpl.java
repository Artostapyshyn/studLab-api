package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Slider;
import com.artostapyshyn.studlabapi.repository.SliderRepository;
import com.artostapyshyn.studlabapi.service.SliderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SliderServiceImpl implements SliderService {

    private final SliderRepository sliderRepository;

    @Override
    public Optional<Slider> findById(Long id) {
        return sliderRepository.findById(id);
    }

    @Override
    public List<Slider> findAll() {
        return sliderRepository.findAll();
    }

    @Override
    public Slider save(Slider slider) {
        return sliderRepository.save(slider);
    }

    @Override
    public void deleteById(Long id) {
        sliderRepository.deleteById(id);
    }
}
