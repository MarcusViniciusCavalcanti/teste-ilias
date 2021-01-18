package com.zonework.atm.struture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageOperationAllotteTriggerDTO {

    private final OperationEnum operation;

    private final AllotteeDTO allotteeDTO;

    @Builder
    @JsonCreator
    public MessageOperationAllotteTriggerDTO(@JsonProperty("operation") OperationEnum operation,
                                             @JsonProperty("allottee") AllotteeDTO allotteeDTO) {
        this.operation = operation;
        this.allotteeDTO = allotteeDTO;
    }

}
