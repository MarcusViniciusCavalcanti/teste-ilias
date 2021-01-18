package com.zonework.atm.integration;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.StringUtils;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

class ConstrainedFields {
    private final ConstraintDescriptions constraintDescriptions;

    ConstrainedFields(Class<?> input) {
        constraintDescriptions = new ConstraintDescriptions(input);
    }

    FieldDescriptor withPath(String path) {
        String delimitedString = StringUtils
            .collectionToDelimitedString(constraintDescriptions.descriptionsForProperty(path), ". ");

        return fieldWithPath(path).attributes(key("constraints").value(delimitedString));
    }
}
