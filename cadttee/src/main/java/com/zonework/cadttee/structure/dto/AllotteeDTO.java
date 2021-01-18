package com.zonework.cadttee.structure.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllotteeDTO {

    private final Integer id;
    private final String name;
    private final String cpf;
    private final String email;
    private final BigDecimal retirementBalance;
    private final int amoutYears;
    private final String status;

    @Builder
    @JsonCreator
    public AllotteeDTO(@JsonProperty("id") Integer id,
                       @JsonProperty("name") String name,
                       @JsonProperty("cpf") String cpf,
                       @JsonProperty("email") String email,
                       @JsonProperty("retirementBalance") BigDecimal retirementBalance,
                       @JsonProperty("status") String status,
                       @JsonProperty("amountYears") int amoutYears) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.retirementBalance = retirementBalance;
        this.status = status;
        this.amoutYears = amoutYears;
    }

}
