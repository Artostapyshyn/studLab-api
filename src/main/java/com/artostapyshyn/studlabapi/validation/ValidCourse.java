package com.artostapyshyn.studlabapi.validation;

import com.artostapyshyn.studlabapi.validation.impl.CourseValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CourseValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCourse {

    String message() default "Course value is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

