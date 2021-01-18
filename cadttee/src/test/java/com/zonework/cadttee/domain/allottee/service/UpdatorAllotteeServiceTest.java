package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.allottee.repository.AllotteeUpateRepository;
import com.zonework.cadttee.structure.message.ReasonMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatorAllotteeServiceTest {

    private static final int ID = 1;

    @Mock
    private AllotteeRepository allotteeRepository;

    @Mock
    private AllotteeUpateRepository allotteeUpateRepository;

    @Mock
    private ExecutorStatusAllotteeService executorStatusAllotteeService;

    @InjectMocks
    private UpdatorAllotteeService updatorAllotteeService;

    @Test
    void shouldReturnException() {
        when(allotteeRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> updatorAllotteeService.editeAllottee(new AllotteInput(), 1));
    }

    @Test
    void shouldHaveUpateEntity() {
        var newName = "new name";
        var newEmail = "new_email@email.com";
        var newCpf = "123456789";
        var newAmountYears = 10;

        var input = new AllotteInput();
        input.setName(newName);
        input.setEmail(newEmail);
        input.setCpf(newCpf);
        input.setAmountYears(newAmountYears);

        var status = new StatusAllotte();
        status.setValue(StatusAllotteEnum.ACTIVE.getStatusEnumName());

        var oldCpf = "98765431";
        var allottee = new Allottee();
        allottee.setCpf(oldCpf);
        allottee.setName("name");
        allottee.setEmail("email@email");
        allottee.setAmoutYears(5);
        allottee.setStatus(status);

        when(allotteeRepository.findById(ID)).thenReturn(Optional.of(allottee));
        when(executorStatusAllotteeService.adjustmentStatusAllottee(
                eq(allottee),
                eq(StatusAllotteEnum.ACTIVE),
                eq(ReasonMessages.MESSAGE_003.getCode()),
                any(Allottee.class), any(Allottee.class))).thenReturn(new Allottee());

        var response = updatorAllotteeService.editeAllottee(input, ID);

        assertAll(() -> {
            assertEquals(newName, response.getName(), "assert name");
            assertEquals(newCpf, response.getCpf(), "assert cpf");
            assertEquals(newAmountYears, response.getAmoutYears(), "assert amout years");
        });

        verify(executorStatusAllotteeService).adjustmentStatusAllottee(
            argThat(entity -> entity.getCpf().equals(newCpf)),
            eq(StatusAllotteEnum.ACTIVE),
            eq(ReasonMessages.MESSAGE_003.getCode()),
            argThat((Allottee oldValue) -> oldValue.getCpf().equals(oldCpf)),
            argThat((Allottee newValue) -> newValue.getCpf().equals(newCpf))
        );
    }
}
