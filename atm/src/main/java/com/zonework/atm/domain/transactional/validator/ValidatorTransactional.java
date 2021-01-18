package com.zonework.atm.domain.transactional.validator;

import com.zonework.atm.application.input.NewTransactionaInput;
import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.domain.allottee.entity.StatusAllotteEnum;
import com.zonework.atm.domain.transactional.entity.TypeTranctional;
import com.zonework.atm.struture.exception.BusinessException;
import com.zonework.atm.struture.message.ReasonMessages;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidatorTransactional {

    public static boolean validate(Allottee allottee, NewTransactionaInput newTransactionaInput) {
        return isActiveAllotte(allottee) && isBalancePositive(allottee, newTransactionaInput);
    }

    private static boolean isBalancePositive(Allottee allottee, NewTransactionaInput newTransactionaInput) {
        if (TypeTranctional.DECREMENT.name().equals(newTransactionaInput.getType())) {
            return allottee.getRetirementBalance().compareTo(newTransactionaInput.getValue()) >= 0;
        }

        return Boolean.TRUE;
    }

    private static boolean isActiveAllotte(Allottee allottee) {
        if (allottee.getStatus() != StatusAllotteEnum.ACTIVE) {
            throw new BusinessException(ReasonMessages.MESSAGE_002.getCode());
        }

        return true;
    }

}
