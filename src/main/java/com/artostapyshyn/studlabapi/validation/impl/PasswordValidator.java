package com.artostapyshyn.studlabapi.validation.impl;

import com.artostapyshyn.studlabapi.validation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z]).{8,}$");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
