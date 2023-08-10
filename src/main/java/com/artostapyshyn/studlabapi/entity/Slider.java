package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "slider")
public class Slider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "slider_photo", nullable = false)
    private byte[] sliderPhoto;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slider slider)) return false;
        return Objects.equals(getId(), slider.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Slider{" +
                "id=" + id +
                ", sliderPhoto=" + Arrays.toString(sliderPhoto) +
                '}';
    }
}