package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.struture.dto.TransactionalDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public abstract class OperationTransactionl {

    public abstract void calculate(TransactionalBalance transactionalBalance, BigDecimal value);

    protected BigDecimal increment(TransactionalBalance transactionalBalance, BigDecimal value) {
        var retirementBalance = transactionalBalance.getAllottee().getRetirementBalance();
        return retirementBalance.add(value);
    }

    protected BigDecimal decrement(TransactionalBalance transactionalBalance, BigDecimal valeu) {
        var retirementBalance = transactionalBalance.getAllottee().getRetirementBalance();
        return retirementBalance.subtract(valeu);
    }
}
