package com.zonework.atm.struture.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class ResponseAllotteeDTO {
    private final String name;
    private final String cpf;
    private final BigDecimal retirement;
    private final BigDecimal retirementValue;
    private final int amountYears;
    private final String status;
}
