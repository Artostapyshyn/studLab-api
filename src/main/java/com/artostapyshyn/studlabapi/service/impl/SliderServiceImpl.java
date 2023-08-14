package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Slider;
import com.artostapyshyn.studlabapi.repository.SliderRepository;
import com.artostapyshyn.studlabapi.service.SliderService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SliderServiceImpl implements SliderService {

    private final SliderRepository sliderRepository;

    @Override
    @Cacheable("slidersById")
    public Optional<Slider> findById(Long id) {
        return sliderRepository.findById(id);
    }

    @Override
    @Cacheable("sliders")
    public List<Slider> findAll() {
        return sliderRepository.findAll();
    }

    @Override
    @Transactional
    @CachePut(value = {"sliders", "slidersById"})
    public Slider save(Slider slider) {
        return sliderRepository.save(slider);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"sliders", "slidersById"})
    public void deleteById(Long id) {
        sliderRepository.deleteById(id);
    }
}
