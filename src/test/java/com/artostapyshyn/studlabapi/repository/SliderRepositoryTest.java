package com.artostapyshyn.studlabapi.repository;

import com.artostapyshyn.studlabapi.entity.Slider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.artostapyshyn.studlabapi.util.TestUtils.createRandomSlider;

@ActiveProfiles("test")
@DataJpaTest
class SliderRepositoryTest {

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByExistingId() {
        Slider slider = createRandomSlider();
        entityManager.persist(slider);
        entityManager.flush();

        Optional<Slider> foundSlider = sliderRepository.findById(slider.getId());

        Assertions.assertTrue(foundSlider.isPresent());
        Assertions.assertEquals(slider.getId(), foundSlider.get().getId());
    }

    @Test
    void findByIdNonExistingId() {
        Optional<Slider> foundSlider = sliderRepository.findById(100L);
        Assertions.assertTrue(foundSlider.isEmpty());
    }
}