package com.zonework.cadttee.domain.allottee.adapter.receive;

import com.zonework.cadttee.domain.allottee.adapter.MessageFormatter;
import com.zonework.cadttee.domain.allottee.service.UpdatorRetirementBalance;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsynchronousProcessingUpateRetimenetForReadingMessageTest {

    @Mock
    private MessageFormatter messageFormatter;

    @Mock
    private UpdatorRetirementBalance updatorRetirementBalance;

    @InjectMocks
    private AsynchronousProcessingUpateRetimenetForReadingMessage readingMessage;

    @Test
    void shoulHaveProcessEntity() {
        var mockTrigger = TransactionalDTO.builder()
            .idAllottee(1)
            .value(10.0)
            .build();

        doNothing()
            .when(updatorRetirementBalance)
            .executeUpdateRetirement(eq(mockTrigger));
        when(messageFormatter.parseMessage(anyString(), eq(TransactionalDTO.class)))
            .thenReturn(mockTrigger);

        readingMessage.onMessage(new MessageTest());

        verify(updatorRetirementBalance).executeUpdateRetirement(eq(mockTrigger));
    }
}
