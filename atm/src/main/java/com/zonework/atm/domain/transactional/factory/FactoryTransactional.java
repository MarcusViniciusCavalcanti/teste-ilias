package com.zonework.atm.domain.transactional.factory;

import com.zonework.atm.application.input.NewTransactionaInput;
import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.entity.TypeTranctional;
import com.zonework.atm.struture.dto.ResponseTransactionalDTO;
import com.zonework.atm.struture.dto.TransactionalDTO;
import com.zonework.atm.struture.dto.TransactionalTypeDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FactoryTransactional {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static TransactionalDTO createDtoByEntity(TransactionalBalance transactionalBalance) {
        return TransactionalDTO.builder()
            .transactionalType(TransactionalTypeDTO.valueOf(transactionalBalance.getType().name()))
            .id(transactionalBalance.getId())
            .idAllottee(transactionalBalance.getAllottee().getId())
            .value(transactionalBalance.getValue().doubleValue())
            .build();
    }

    public static TransactionalBalance cloneBaseData(TransactionalBalance transactionalBalance) {
        var clone = new TransactionalBalance();
        clone.setType(transactionalBalance.getType());
        clone.setAllottee(transactionalBalance.getAllottee());
        clone.setValue(transactionalBalance.getValue());

        return clone;
    }

    public static ResponseTransactionalDTO createResponse(TransactionalBalance transactionalBalance) {
        return ResponseTransactionalDTO.builder()
            .id(transactionalBalance.getId())
            .date(DATE_TIME_FORMATTER.format(transactionalBalance.getDate()))
            .idAllottee(transactionalBalance.getAllottee().getId())
            .status(transactionalBalance.getStatus().getDescription())
            .value(transactionalBalance.getValue().doubleValue())
            .transactionalType(TransactionalTypeDTO.valueOf(transactionalBalance.getType().name()))
            .build();
    }

    public static TransactionalBalance createEntityByAllotteAndInputAndStatus(NewTransactionaInput input,
                                                                              Allottee allottee,
                                                                              StatusTransactionalEnum status) {
        var transactional = new TransactionalBalance();
        transactional.setAllottee(allottee);
        transactional.setType(TypeTranctional.valueOf(input.getType()));
        transactional.setValue(input.getValue());
        transactional.setStatus(status);

        return transactional;
    }
}
