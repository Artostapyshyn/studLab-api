package com.artostapyshyn.studlabapi.validation.impl;

import com.artostapyshyn.studlabapi.validation.ValidCourse;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CourseValidator implements ConstraintValidator<ValidCourse, String> {

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            int courseNumber = Integer.parseInt(value);
            return courseNumber >= 1 && courseNumber <= 6;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
