package com.zonework.atm.struture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CompleteTransactionalTriggerDTO {
    private final Integer idTransactional;
    private final OperationEnum operation;

    @Builder
    @JsonCreator
    public CompleteTransactionalTriggerDTO(@JsonProperty("idTransactional") Integer idTransactional,
                                           @JsonProperty("operation") OperationEnum operation) {
        this.idTransactional = idTransactional;
        this.operation = operation;
    }

}
