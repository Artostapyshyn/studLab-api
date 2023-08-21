package com.artostapyshyn.studlabapi.validation;

import com.artostapyshyn.studlabapi.validation.impl.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "Password code is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
