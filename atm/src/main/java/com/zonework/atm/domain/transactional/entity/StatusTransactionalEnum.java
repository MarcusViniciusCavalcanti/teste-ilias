package com.zonework.atm.domain.transactional.entity;

import lombok.Getter;

@Getter
public enum StatusTransactionalEnum {
    COMPLETE("Completado"), SEND("Enviado"), AWATTING("Aguardando"), ACCEPTED("aceito");

    private final String description;

    StatusTransactionalEnum(String description) {
        this.description = description;
    }
}
