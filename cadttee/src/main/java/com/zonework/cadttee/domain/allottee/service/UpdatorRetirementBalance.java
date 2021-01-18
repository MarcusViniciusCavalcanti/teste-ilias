package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingMessageSendingCompleteTransactional;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.history.service.CreatorHistoryOperationAllotteeService;
import com.zonework.cadttee.structure.dto.OperationEnum;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import com.zonework.cadttee.structure.message.ReasonMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@MockitoSettings(strictness = Strictness.LENIENT)
public class UpdatorRetirementBalance {
    private static final String MESSAGE = "Allottee not found by: %d";

    private final CreatorHistoryOperationAllotteeService creatorHistoryOperationAllotteeService;
    private final AllotteeRepository allotteeRepository;
    private final BeanFactory beanFactory;
    private final AsynchronousProcessingMessageSendingCompleteTransactional sendingCompleteTransactional;

    @Transactional(propagation = Propagation.MANDATORY)
    public void executeUpdateRetirement(TransactionalDTO transactionalDTO) {
        var allottee = allotteeRepository.findById(transactionalDTO.getIdAllottee())
            .orElseThrow(() -> new EntityNotFoundException(String.format(MESSAGE, transactionalDTO.getIdAllottee())));

        try {
            var operation = transactionalDTO.getTransactionalType().name();

            log.info("Update retirement from {}", operation);
            var oldValue = allottee.getRetirementBalance();
            configureNewBalanceValue(transactionalDTO, allottee);

            allotteeRepository.save(allottee);

            createNewHistoryToOperation(allottee, operation, oldValue);

            sendingCompleteTransactional.scheduleAsynchronousProcessing(transactionalDTO, OperationEnum.UPDATE);
        } catch (BeansException exception) {
            log.error("Operation invalid {}", transactionalDTO.getTransactionalType());
            throw new RuntimeException("Operation ilegal");
        }
    }

    private void createNewHistoryToOperation(Allottee allottee,
                                             String operation,
                                             BigDecimal oldValue) {
        var reasonOperation = ReasonMessages.MESSAGE_002.getCode();
        var newValue = allottee.getRetirementBalance();

        creatorHistoryOperationAllotteeService.create(
            allottee,
            reasonOperation,
            operation,
            oldValue,
            newValue
        );
    }

    private static void configureNewBalanceValue(TransactionalDTO transactionalDTO,
                                                 Allottee allottee) {
        var newRetirementBalance = BigDecimal.valueOf(transactionalDTO.getValue());
        allottee.setRetirementBalance(newRetirementBalance);
    }
}
