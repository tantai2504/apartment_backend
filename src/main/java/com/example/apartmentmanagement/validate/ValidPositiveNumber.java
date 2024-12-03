package com.example.apartmentmanagement.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PositiveNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPositiveNumber {
    String message() default "Must be a positive number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
