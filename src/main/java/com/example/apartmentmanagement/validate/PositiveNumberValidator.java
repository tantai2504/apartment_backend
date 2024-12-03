package com.example.apartmentmanagement.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveNumberValidator implements ConstraintValidator<ValidPositiveNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
