package com.artostapyshyn.studlabapi.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntervalTest {

    @Test
    void values_shouldReturnAllIntervals() {
        Interval[] intervals = Interval.values();
        assertEquals(4, intervals.length);
        assertEquals(Interval.DAY, intervals[0]);
        assertEquals(Interval.WEEK, intervals[1]);
        assertEquals(Interval.MONTH, intervals[2]);
        assertEquals(Interval.ALL_TIME, intervals[3]);
    }

    @Test
    void valueOf_withValidIntervalName_shouldReturnInterval() {
        Interval interval = Interval.valueOf("WEEK");
        assertEquals(Interval.WEEK, interval);
    }

    @Test
    void valueOf_withInvalidIntervalName_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Interval.valueOf("YEAR"));
    }
}
