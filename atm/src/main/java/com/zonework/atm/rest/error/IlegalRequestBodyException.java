package com.zonework.atm.rest.error;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

public class IlegalRequestBodyException extends RuntimeException {
    private final String title;
    private final BindingResult resultSet;

    public IlegalRequestBodyException(String invalidBody, BindingResult resultSet) {
        super(invalidBody);
        title = invalidBody;
        this.resultSet = resultSet;
    }

    public List<FieldError> errors() {
        if (resultSet.hasErrors()) {
            return resultSet.getFieldErrors();
        }
        return Collections.emptyList();
    }

    public String getTitle() {
        return title;
    }
}
