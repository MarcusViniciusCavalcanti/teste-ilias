package com.zonework.cadttee.domain.allottee.delegate;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.service.ExecutorStatusAllotteeService;
import com.zonework.cadttee.structure.message.ReasonMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPendingRegistrationTest {

    @Mock
    private ExecutorStatusAllotteeService statusAllotteeService;

    @InjectMocks
    private ProcessPendingRegistration processPendingRegistration;

    @Test
    void shouldProcessActive() {
        var allottee = new Allottee();

        when(statusAllotteeService.adjustmentStatusAllottee(
                eq(allottee),
                eq(StatusAllotteEnum.ACTIVE),
                eq(ReasonMessages.MESSAGE_001.getCode())
            )).thenReturn(allottee);

        processPendingRegistration.process(allottee);

        verify(statusAllotteeService).adjustmentStatusAllottee(eq(allottee),
            eq(StatusAllotteEnum.ACTIVE), eq(ReasonMessages.MESSAGE_001.getCode()));
    }
}
