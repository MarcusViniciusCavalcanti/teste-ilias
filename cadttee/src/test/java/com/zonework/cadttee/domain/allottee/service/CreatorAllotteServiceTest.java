package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.structure.exception.BusinessException;
import com.zonework.cadttee.structure.message.ReasonMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import javax.persistence.EntityExistsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatorAllotteServiceTest {

    private static final String MESSAGE_006 = "MESSAGE-006";
    private static final String CPF = "12345678";
    private static final String EMAIL = "email@email.com";
    private static final int AMOUNT_YEARS = 10;
    private static final String NAME = "Name";

    @Mock
    private AllotteeRepository allotteeRepository;

    @Mock
    private ExecutorStatusAllotteeService executorStatusAllotteeService;

    @InjectMocks
    private CreatorAllotteService creatorAllotteService;

    @Test
    void shouldReturnBussinesExceptionWhenStatusAllotteeNotAllowsRegistration() {
        var status = new StatusAllotte();
        var allottee = new Allottee();

        status.setValue(StatusAllotteEnum.REGISTRATION_PENDING.getStatusEnumName());
        status.allowsRegistry(Boolean.FALSE);
        allottee.setStatus(status);

        when(allotteeRepository.findByCpf(eq(CPF))).thenReturn(Optional.of(allottee));

        var execption = assertThrows(BusinessException.class, () -> {
            var allotteInput = new AllotteInput();
            allotteInput.setCpf(CPF);
            creatorAllotteService.active(allotteInput);
        });

        assertEquals(MESSAGE_006, execption.getCode());
    }

    @Test
    void shouldReturnNewAllotteeWhenNotFoundAllotteInDatabase() {
        var input = new AllotteInput();
        input.setCpf(CPF);
        input.setEmail(EMAIL);
        input.setAmountYears(AMOUNT_YEARS);
        input.setName(NAME);

        var status = new StatusAllotte();
        status.setValue(StatusAllotteEnum.REGISTRATION_PENDING.getStatusEnumName());
        status.allowsRegistry(Boolean.FALSE);

        var allottee = new Allottee();
        allottee.setStatus(status);

        when(allotteeRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(executorStatusAllotteeService
            .adjustmentStatusAllottee(
                any(Allottee.class),
                eq(StatusAllotteEnum.REGISTRATION_PENDING),
                eq(ReasonMessages.MESSAGE_000.getCode())))
            .thenReturn(allottee);

        creatorAllotteService.active(input);

        var orderExecution = inOrder(allotteeRepository, executorStatusAllotteeService);

        verify(allotteeRepository).findByCpf(CPF);
        verify(executorStatusAllotteeService).adjustmentStatusAllottee(
            argThat(entity -> entity.getCpf().equals(CPF) && entity.getRetirementBalance().doubleValue() == 0.0),
            argThat(statusArgs -> statusArgs == StatusAllotteEnum.REGISTRATION_PENDING),
            argThat(other -> ReasonMessages.MESSAGE_000.getCode().equals(other))
        );
    }

    @Test
    void shouldReturnNewAllotteeWhenExistsInAllotteInDatabase() {
        var input = new AllotteInput();
        input.setCpf(CPF);
        input.setEmail(EMAIL);
        input.setAmountYears(AMOUNT_YEARS);
        input.setName(NAME);

        var status = new StatusAllotte();
        status.setValue(StatusAllotteEnum.EXCLUDED.getStatusEnumName());
        status.allowsRegistry(Boolean.TRUE);

        var allottee = new Allottee();
        allottee.setStatus(status);
        allottee.setCpf(CPF);

        when(allotteeRepository.findByCpf(any())).thenReturn(Optional.of(allottee));
        when(executorStatusAllotteeService
            .adjustmentStatusAllottee(
                any(Allottee.class),
                eq(StatusAllotteEnum.REGISTRATION_PENDING),
                eq(ReasonMessages.MESSAGE_000.getCode())))
            .thenReturn(allottee);

        creatorAllotteService.active(input);

        var orderExecution = inOrder(allotteeRepository, executorStatusAllotteeService);

        verify(allotteeRepository).findByCpf(CPF);
        verify(executorStatusAllotteeService).adjustmentStatusAllottee(
            argThat(entity -> entity.getCpf().equals(CPF) && entity.getRetirementBalance().doubleValue() == 0.0),
            argThat(statusArgs -> statusArgs == StatusAllotteEnum.REGISTRATION_PENDING),
            argThat(other -> ReasonMessages.MESSAGE_000.getCode().equals(other))
        );
    }

    @Test
    void shouldReturnExistEntity() {
        var status = new StatusAllotte();
        var allottee = new Allottee();

        status.setValue(StatusAllotteEnum.ACTIVE.getStatusEnumName());
        status.allowsRegistry(Boolean.FALSE);
        allottee.setStatus(status);

        when(executorStatusAllotteeService.adjustmentStatusAllottee(any(Allottee.class),
                                                                    any(StatusAllotteEnum.class),
                                                                    anyString()))
            .thenThrow(new DataIntegrityViolationException(""));

        assertThrows(EntityExistsException.class, () -> {
            var allotteInput = new AllotteInput();
            allotteInput.setCpf(CPF);
            allotteInput.setAmountYears(0);
            creatorAllotteService.create(allotteInput);
        });

    }
}
