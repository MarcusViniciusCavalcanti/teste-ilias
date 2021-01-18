package com.zonework.atm.domain.config.beans;

import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import com.zonework.atm.domain.transactional.service.CalculatorBalance;
import com.zonework.atm.domain.transactional.service.ExecutorDecremetValueBalance;
import com.zonework.atm.domain.transactional.service.ExecutorIncreametValueBalance;
import com.zonework.atm.domain.transactional.service.ExecutorReversalValueBalance;
import com.zonework.atm.domain.transactional.service.OperationTransactionl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperationTransactionalConfigurations {
    public static final String INCREMENT_OPERATION = "INCREMENT";
    public static final String DECREMENT_OPERATION = "DECREMENT";
    public static final String REVERSAL_OPERATION = "REVERSAL";


    @Bean(INCREMENT_OPERATION)
    public OperationTransactionl incrementOperation(CalculatorBalance calculatorBalance,
                                                    TransactionalRepository transactionalRepository) {
        return new ExecutorIncreametValueBalance(calculatorBalance, transactionalRepository);
    }

    @Bean(DECREMENT_OPERATION)
    public OperationTransactionl decrementOperation(CalculatorBalance calculatorBalance,
                                                    TransactionalRepository transactionalRepository) {
        return new ExecutorDecremetValueBalance(calculatorBalance, transactionalRepository);
    }

    @Bean(REVERSAL_OPERATION)
    public OperationTransactionl reversalOperation(CalculatorBalance calculatorBalance,
                                                    TransactionalRepository transactionalRepository) {
        return new ExecutorReversalValueBalance(calculatorBalance, transactionalRepository);
    }
}
