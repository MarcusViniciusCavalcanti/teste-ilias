package com.zonework.atm.struture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TransactionalDTO {
    private final Double value;
    private final Integer id;
    private final Integer idAllottee;
    private final TransactionalTypeDTO transactionalType;

    @JsonCreator
    @Builder
    public TransactionalDTO(@JsonProperty("value") Double value,
                            @JsonProperty("id") Integer id,
                            @JsonProperty("idAllottee") Integer idAllottee,
                            @JsonProperty("type") TransactionalTypeDTO transactionalType) {
        this.value = value;
        this.id = id;
        this.idAllottee = idAllottee;
        this.transactionalType = transactionalType;
    }

}
