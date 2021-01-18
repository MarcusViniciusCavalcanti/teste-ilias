package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingMessageSendingCompleteTransactional;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.history.service.CreatorHistoryOperationAllotteeService;
import com.zonework.cadttee.structure.dto.OperationEnum;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import com.zonework.cadttee.structure.dto.TransactionalTypeDTO;
import com.zonework.cadttee.structure.message.ReasonMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatorRetirementBalanceTest {

    private static final int ID = 1;
    private static final double VALUE = 10.0;
    private static final String REASON = "code";
    private static final BigDecimal TEN = BigDecimal.TEN;

    @Mock
    private CreatorHistoryOperationAllotteeService creatorHistoryOperationAllotteeService;

    @Mock
    private AllotteeRepository allotteeRepository;

    @Mock
    private AsynchronousProcessingMessageSendingCompleteTransactional sendingCompleteTransactional;

    @InjectMocks
    private UpdatorRetirementBalance updatorRetirementBalance;

    @Test
    void shouldReturnExceptionNotFoundAllootee() {
        when(allotteeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            var dto = new TransactionalDTO(VALUE, ID, ID, TransactionalTypeDTO.INCREMENT);
            updatorRetirementBalance.executeUpdateRetirement(dto);
        });
    }

    @Test
    void shouldHaveSetterNewBalance() {
        var transactionalTypeDTO = TransactionalTypeDTO.INCREMENT;
        var transactional = new TransactionalDTO(VALUE, ID ,ID, transactionalTypeDTO);

        var status = new StatusAllotte();
        status.setValue(StatusAllotteEnum.ACTIVE.getStatusEnumName());

        var allottee = new Allottee();
        allottee.setCpf("98765431");
        allottee.setName("name");
        allottee.setEmail("email@email");
        allottee.setAmoutYears(5);
        allottee.setStatus(status);
        allottee.setRetirementBalance(BigDecimal.ONE);


        when(allotteeRepository.findById(ID)).thenReturn(Optional.of(allottee));
        doNothing()
            .when(creatorHistoryOperationAllotteeService)
            .create(
                allottee,
                ReasonMessages.MESSAGE_002.getCode(),
                "INCREMENT",
                allottee.getRetirementBalance(),
                BigDecimal.valueOf(VALUE)
            );
        doReturn(allottee).when(allotteeRepository).save(eq(allottee));
        doNothing()
            .when(sendingCompleteTransactional)
            .scheduleAsynchronousProcessing(any(TransactionalDTO.class), any(OperationEnum.class));

        updatorRetirementBalance.executeUpdateRetirement(transactional);

        verify(allotteeRepository).findById(any());
        verify(allotteeRepository).save(any(Allottee.class));
        verify(sendingCompleteTransactional).scheduleAsynchronousProcessing(any(), any());
    }

}
