package com.zonework.atm.struture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TransactionalReceiveTriggerDTO {
    private final Integer id;

    @Builder
    @JsonCreator
    public TransactionalReceiveTriggerDTO(@JsonProperty("id") Integer id) {
        this.id = id;
    }

}
