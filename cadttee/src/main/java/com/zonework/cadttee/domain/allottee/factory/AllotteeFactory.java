package com.zonework.cadttee.domain.allottee.factory;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AllotteeFactory {

    public static AllotteeDTO buildEntityByAllottee(Allottee allottee) {
        return AllotteeDTO.builder()
            .id(allottee.getId())
            .amoutYears(allottee.getAmoutYears())
            .cpf(allottee.getCpf())
            .email(allottee.getEmail())
            .name(allottee.getName())
            .status(allottee.getStatus().getValue())
            .retirementBalance(allottee.getRetirementBalance())
            .build();
    }

    public static Allottee buildEntityByInput(AllotteInput input) {
        var allotte = new Allottee();

        allotte.setCpf(input.getCpf());
        allotte.setEmail(input.getEmail());
        allotte.setAmoutYears(input.getAmountYears());
        allotte.setName(input.getName());

        return allotte;
    }

    public static void replaceBy(Allottee allottee, AllotteInput allotteInput) {
        allottee.setName(allotteInput.getName());
        allottee.setEmail(allotteInput.getEmail());
        allottee.setCpf(allotteInput.getCpf());
        allottee.setAmoutYears(allotteInput.getAmountYears());
    }

    public static Allottee clone(Allottee allottee, AllotteInput allotteInput) {
        var clone = new Allottee();
        clone.setName(allottee.getName());
        clone.setEmail(allottee.getEmail());
        clone.setCpf(allottee.getCpf());
        clone.setAmoutYears(allottee.getAmoutYears());
        clone.setRetirementBalance(allottee.getRetirementBalance());
        clone.setId(allottee.getId());

        return clone;
    }

}
