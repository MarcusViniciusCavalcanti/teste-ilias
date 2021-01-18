package com.zonework.atm.domain.factory;

import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.struture.dto.ResponseAllotteeDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AllotteeFactory {

    public static ResponseAllotteeDTO buildResponse(Allottee allottee) {
        return ResponseAllotteeDTO.builder()
            .amountYears(allottee.getAmoutYears())
            .cpf(allottee.getCpf())
            .name(allottee.getName())
            .retirement(allottee.getRetirementBalance())
            .retirementValue(allottee.getRetirementValue())
            .status(allottee.getStatus().name())
            .build();
    }
}
