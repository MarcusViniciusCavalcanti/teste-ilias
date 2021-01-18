package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.structure.message.ReasonMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutorExclutionAllotteeServiceTest {
    private static final int ID = 12;

    @Mock
    private AllotteeRepository allotteeRepository;

    @Mock
    private ExecutorStatusAllotteeService executorStatusAllotteeService;

    @InjectMocks
    private ExecutorExclutionAllotteeService executorExclutionAllotteeService;

    @Test
    void shouldReturnExceptionWhenNotFoundInDatabase() {
        when(allotteeRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> executorExclutionAllotteeService.remove(ID));
    }

    @Test
    void shouldHaveRemoveOnDatabase() {
        var allottee = new Allottee();
        allottee.setId(ID);

        when(allotteeRepository.findById(eq(ID))).thenReturn(Optional.of(allottee));
        when(executorStatusAllotteeService
            .adjustmentStatusAllottee(
                any(Allottee.class),
                eq(StatusAllotteEnum.EXCLUDED),
                eq(ReasonMessages.MESSAGE_005.getCode())))
            .thenReturn(allottee);

        executorExclutionAllotteeService.remove(ID);

        var orderExecution = inOrder(allotteeRepository, executorStatusAllotteeService);

        verify(allotteeRepository).findById(ID);
        verify(executorStatusAllotteeService).adjustmentStatusAllottee(
            argThat(entity -> entity.getId().equals(ID)),
            argThat(statusArgs -> statusArgs == StatusAllotteEnum.EXCLUDED),
            argThat(other -> ReasonMessages.MESSAGE_005.getCode().equals(other))
        );
    }
}
