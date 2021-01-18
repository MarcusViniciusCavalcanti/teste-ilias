package com.zonework.atm.application.input;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Setter
@Getter
public class NewTransactionaInput {
    @NotNull
    @Positive
    private Integer id;

    @NotNull
    @Positive
    private BigDecimal value;

    @NotNull
    @Pattern(regexp = "^(INCREMENT|DECREMENT)$")
    private String type;
}
