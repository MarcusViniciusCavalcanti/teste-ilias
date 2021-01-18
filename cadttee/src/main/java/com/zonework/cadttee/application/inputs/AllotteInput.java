package com.zonework.cadttee.application.inputs;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class AllotteInput {

    @NotBlank
    private String name;

    @NotBlank
    private String cpf;

    @NotBlank
    private String email;

    @Positive
    private Integer amountYears;

}
