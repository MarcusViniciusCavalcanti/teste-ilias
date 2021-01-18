package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.domain.adapter.send.AsynchronousProcessingWithTopicForSendingMessage;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import com.zonework.atm.struture.dto.TransactionalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExecutorUpdateRetirementBalance {
    private final TransactionalRepository transactionalRepository;
    private final BeanFactory beanFactory;
    private final AsynchronousProcessingWithTopicForSendingMessage sendingMessage;

    @Transactional(propagation = Propagation.MANDATORY)
    public void execute(Integer id) {
        log.info("Running update retirement");
        log.debug("By id {}", id);

        transactionalRepository.findById(id)
            .ifPresentOrElse(
                transactionalBalance -> {
                    calculateNewPalance(transactionalBalance);
                    updateTransactionStatusAwattingResponse(transactionalBalance);
                },
                () -> log.warn("Transactional not found by id {}", id)
            );
    }

    private void updateTransactionStatusAwattingResponse(TransactionalBalance transactionalBalance) {
        log.debug("Running Update status transaction to AWATTING");
        transactionalBalance.setStatus(StatusTransactionalEnum.AWATTING);
        sendingMessage.scheduleAsynchronousProcessing(transactionalBalance);
        transactionalRepository.saveAndFlush(transactionalBalance);
    }

    private void calculateNewPalance(TransactionalBalance transactionalBalance) {
        try {
            log.debug("Running calculate new retirement balance");
            var operator = beanFactory.getBean(transactionalBalance.getType().name(), OperationTransactionl.class);
            operator.calculate(transactionalBalance, transactionalBalance.getValue());
        } catch (BeansException exception) {
            log.error("Operation invalid {}", transactionalBalance.getType());
            throw new RuntimeException("Operation ilegal");
        }
    }

}
