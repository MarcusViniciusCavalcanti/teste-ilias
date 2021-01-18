package com.zonework.atm.struture.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseTransactionalDTO {
    private final Double value;
    private final Integer id;
    private final Integer idAllottee;
    private final String date;
    private final TransactionalTypeDTO transactionalType;
    private final String status;
}
