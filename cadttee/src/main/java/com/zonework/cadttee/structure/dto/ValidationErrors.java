package com.zonework.cadttee.structure.dto;

import lombok.Data;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ValidationErrors {

    private List<Field> fieldErrors;

    public ValidationErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors.stream().map(Field::new).collect(Collectors.toList());
    }

    @Data
    public static class Field {
        private String message;
        private String field;
        private Object rejectedValue;
        private String code;

        public Field(FieldError objectError) {
            code = objectError.getCode();
            field = objectError.getField();
            message = objectError.getDefaultMessage();
            rejectedValue = objectError.getRejectedValue();
        }
    }
}
