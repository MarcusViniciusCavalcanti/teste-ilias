package com.zonework.atm.struture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class AllotteeActiveTrigger {
    private final Integer id;
    private final OperationEnum operation;
    private final String value;

    @Builder
    @JsonCreator
    public AllotteeActiveTrigger(@JsonProperty("id") Integer id,
                                 @JsonProperty("operation") OperationEnum operation,
                                 @JsonProperty("value") String value) {
        this.id = id;
        this.operation = operation;
        this.value = value;
    }
}
