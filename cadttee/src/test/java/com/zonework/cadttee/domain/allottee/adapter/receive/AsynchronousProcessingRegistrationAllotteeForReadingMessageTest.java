package com.zonework.cadttee.domain.allottee.adapter.receive;

import com.zonework.cadttee.domain.allottee.adapter.MessageFormatter;
import com.zonework.cadttee.domain.allottee.delegate.ProcessPendingRegistration;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import com.zonework.cadttee.structure.dto.MessageOperationAllotteTriggerDTO;
import com.zonework.cadttee.structure.dto.OperationEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsynchronousProcessingRegistrationAllotteeForReadingMessageTest {

    @Mock
    private MessageFormatter messageFormatter;

    @Mock
    private ProcessPendingRegistration processPendingRegistration;

    @Mock
    private AllotteeRepository allotteeRepository;

    @InjectMocks
    private AsynchronousProcessingRegistrationAllotteeForReadingMessage readingMessage;

    @Test
    void shoulHaveProcessEntity() {
        var mockTrigger = MessageOperationAllotteTriggerDTO.builder()
            .operation(OperationEnum.UPDATE)
            .allotteeDTO(AllotteeDTO.builder().id(1).build())
            .build();

        var mockAllottee = new Allottee();
        mockAllottee.setStatus(new StatusAllotte());
        mockAllottee.setName("Name");
        mockAllottee.setRetirementBalance(BigDecimal.TEN);
        mockAllottee.setEmail("email@email.com");
        mockAllottee.setAmoutYears(10);
        mockAllottee.setCpf("12345678901");

        when(allotteeRepository.findById(anyInt())).thenReturn(Optional.of(mockAllottee));
        doNothing()
            .when(processPendingRegistration)
            .process(eq(mockAllottee));
        when(messageFormatter.parseMessage(anyString(), eq(MessageOperationAllotteTriggerDTO.class)))
            .thenReturn(mockTrigger);

        readingMessage.onMessage(new MessageTest());

        verify(allotteeRepository).findById(anyInt());
        verify(processPendingRegistration).process(eq(mockAllottee));
    }
}
