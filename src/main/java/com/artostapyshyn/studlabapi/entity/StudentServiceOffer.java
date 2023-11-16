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
@Table(name = "student_services", indexes = {
        @Index(name = "idx_student_services", columnList = "provider_id")
})
public class StudentServiceOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "name_of_service", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "service_photo")
    private byte[] photoBytes;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Student provider;

    @Column(name = "telegram")
    private String telegram;

    @Column(name = "viber")
    private String viber;

    @Column(name = "whatsapp")
    private String whatsapp;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentServiceOffer that)) return false;
        return Double.compare(getRating(), that.getRating()) == 0 && Objects.equals(getId(), that.getId()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getName(), that.getName()) && Objects.equals(getPrice(), that.getPrice()) && Arrays.equals(getPhotoBytes(), that.getPhotoBytes());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getDescription(), getName(), getPrice(), getRating());
        result = 31 * result + Arrays.hashCode(getPhotoBytes());
        return result;
    }
}
