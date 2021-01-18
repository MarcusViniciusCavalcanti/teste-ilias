package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.domain.allottee.repository.AllotteeRepository;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.struture.utils.TransactionRunnableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CalculatorBalance {
    private static final long AMOUNT_MONTH = 12L;

    private final AllotteeRepository allotteeRepository;
    private final TransactionRunnableHelper transactionRunnableHelper;

    @Transactional
    public void calculateNewValue(TransactionalBalance transactionalBalance) {
        var allottee = calulcateRetirement(transactionalBalance.getAllottee());
        allotteeRepository.saveAndFlush(allottee);
    }

    public Allottee calulcateRetirement(Allottee allottee) {
        var value = allottee.getRetirementBalance();
        var years = allottee.getAmoutYears();
        var divider = years * AMOUNT_MONTH;
        var newRetirement = value.divide(BigDecimal.valueOf(divider), MathContext.DECIMAL32);

        allottee.setRetirementValue(newRetirement);

        return allottee;
    }

}
