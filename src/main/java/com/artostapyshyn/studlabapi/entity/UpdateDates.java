package com.artostapyshyn.studlabapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateDates that)) return false;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getLastUpdateDate(), that.getLastUpdateDate())
                && Objects.equals(getLastUpdateWeek(), that.getLastUpdateWeek())
                && Objects.equals(getLastUpdateMonth(), that.getLastUpdateMonth());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLastUpdateDate(), getLastUpdateWeek(), getLastUpdateMonth());
    }

    @Override
    public String toString() {
        return "UpdateDates{" +
                "id=" + id +
                ", lastUpdateDate=" + lastUpdateDate +
                ", lastUpdateWeek=" + lastUpdateWeek +
                ", lastUpdateMonth=" + lastUpdateMonth +
                '}';
    }
}