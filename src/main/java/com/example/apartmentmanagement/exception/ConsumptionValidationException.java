package com.example.apartmentmanagement.exception;

import java.util.List;

public class ConsumptionValidationException extends Exception{
    private final List<String> errors;

    public ConsumptionValidationException(List<String> errors) {
        super("Excel validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
