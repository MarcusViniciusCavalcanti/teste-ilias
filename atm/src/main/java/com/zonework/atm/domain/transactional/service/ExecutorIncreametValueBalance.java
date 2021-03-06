package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.domain.config.beans.OperationTransactionalConfigurations;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Qualifier(OperationTransactionalConfigurations.INCREMENT_OPERATION)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExecutorIncreametValueBalance extends OperationTransactionl{
    private final CalculatorBalance calculatorBalance;
    private final TransactionalRepository transactionalRepository;

    @Override
    public void calculate(TransactionalBalance transactionalBalance, BigDecimal value) {
        var newRetirement = increment(transactionalBalance, value);
        transactionalBalance.getAllottee().setRetirementBalance(newRetirement);

        var transactionalPersistent = transactionalRepository.saveAndFlush(transactionalBalance);

        calculatorBalance.calculateNewValue(transactionalPersistent);
    }

}
