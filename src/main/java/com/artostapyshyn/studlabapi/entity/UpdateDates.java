package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "update_dates")
public class UpdateDates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    @Column(name = "last_update_week")
    private LocalDate lastUpdateWeek;

    @Column(name = "last_update_month")
    private YearMonth lastUpdateMonth;

}