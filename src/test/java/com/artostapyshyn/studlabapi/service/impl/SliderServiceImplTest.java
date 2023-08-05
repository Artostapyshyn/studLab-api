package com.artostapyshyn.studlabapi.service.impl;

import com.artostapyshyn.studlabapi.entity.Slider;
import com.artostapyshyn.studlabapi.repository.SliderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class SliderServiceImplTest {

    @Mock
    private SliderRepository sliderRepository;

    @InjectMocks
    private SliderServiceImpl sliderService;

    @Test
    void save() {
        Slider slider = new Slider();

        when(sliderRepository.save(slider)).thenReturn(slider);

        Slider savedSlider = sliderService.save(slider);
        assertNotNull(savedSlider);
        assertEquals(savedSlider, slider);

        verify(sliderRepository, times(1)).save(slider);
    }

    @Test
    void findById() {
        Slider slider = new Slider();

        when(sliderRepository.findById(slider.getId())).thenReturn(Optional.of(slider));

        Optional<Slider> optionalSlider = sliderService.findById(slider.getId());
        Slider resultSlider = optionalSlider.orElse(null);

        assertNotNull(slider);
        assertEquals(slider, resultSlider);
        verify(sliderRepository, times(1)).findById(slider.getId());
    }

    @Test
    void findAll() {
        List<Slider> sliders = List.of(new Slider(), new Slider(), new Slider());

        when(sliderRepository.findAll()).thenReturn(sliders);

        List<Slider> resultList = sliderService.findAll();
        assertNotEquals(sliders.size(), 1);
        assertEquals(sliders.size(), resultList.size());
        assertNotNull(sliders);

        verify(sliderRepository, times(1)).findAll();
    }

    @Test
    void deleteById() {
        Long sliderId = 1L;
        sliderService.deleteById(sliderId);
        verify(sliderRepository, times(1)).deleteById(sliderId);
    }
}
