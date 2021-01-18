package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingMessageSendingOperationAllotte;
import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingWithTopicForSendingMessage;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.allottee.repository.StatusAllotteeRepository;
import com.zonework.cadttee.domain.history.service.CreatorHistoryOperationAllotteeService;
import com.zonework.cadttee.structure.message.ReasonMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutorStatusAllotteeServiceTest {
    private static final String CPF = "12345678";
    private static final String ARGS_ONE = "one";
    private static final String ARGS_TWO = "two";

    @Mock
    private AllotteeRepository allotteeRepository;

    @Mock
    private StatusAllotteeRepository statusAllotteeRepository;

    @Mock
    private CreatorHistoryOperationAllotteeService creatorHistoryOperationAllotteeService;

    @Mock
    private AsynchronousProcessingWithTopicForSendingMessage processingWithTopicForSendingMessage;

    @Mock
    private AsynchronousProcessingMessageSendingOperationAllotte processingMessageSendingOperationAllotte;

    @InjectMocks
    private ExecutorStatusAllotteeService executorStatusAllotteeService;

    @Test
    void shouldReturnExceptionWhenNotFound() {
        when(statusAllotteeRepository.findById(eq(StatusAllotteEnum.EXCLUDED.getStatusID()))).thenReturn(Optional.empty());

        var allottee = new Allottee();
        allottee.setCpf(CPF);


        assertThrows(EntityNotFoundException.class, () ->
            executorStatusAllotteeService
                .adjustmentStatusAllottee(allottee, StatusAllotteEnum.EXCLUDED, ReasonMessages.MESSAGE_000.getCode()));
    }

    @Test
    void shouldSetterNewStatusWithoutArgsParameters() {
        var allottee = new Allottee();
        var active = StatusAllotteEnum.ACTIVE;
        var code = ReasonMessages.MESSAGE_000.getCode();
        var status = new StatusAllotte();
        status.setId(active.getStatusID());
        status.setValue(active.getStatusEnumName());
        status.allowsAsynchronousProcessing(Boolean.FALSE);
        status.publish(Boolean.FALSE);

        when(statusAllotteeRepository.findById(eq(active.getStatusID()))).thenReturn(Optional.of(status));
        when(allotteeRepository.save(any(Allottee.class))).thenReturn(allottee);
        doNothing().when(creatorHistoryOperationAllotteeService).create(any(Allottee.class), eq(code), eq(active.operation().name()));

        executorStatusAllotteeService.adjustmentStatusAllottee(allottee, active, code);

       var orderExecution =
           inOrder(statusAllotteeRepository, allotteeRepository, creatorHistoryOperationAllotteeService, processingWithTopicForSendingMessage);

       orderExecution.verify(statusAllotteeRepository).findById(active.getStatusID());
       orderExecution.verify(allotteeRepository).save(argThat(entity -> entity.getStatus().getValue().equals(active.getStatusEnumName())));
       orderExecution.verify(creatorHistoryOperationAllotteeService).create(any(Allottee.class), eq(code), eq(active.operation().name()));
    }

    @Test
    void shouldSetterNewStatusWithArgsParameters() {
        var allottee = new Allottee();
        var active = StatusAllotteEnum.ACTIVE;
        var code = ReasonMessages.MESSAGE_000.getCode();
        var status = new StatusAllotte();
        status.setId(active.getStatusID());
        status.setValue(active.getStatusEnumName());
        status.allowsAsynchronousProcessing(Boolean.FALSE);
        status.publish(Boolean.FALSE);

        when(statusAllotteeRepository.findById(eq(active.getStatusID()))).thenReturn(Optional.of(status));
        when(allotteeRepository.save(any(Allottee.class))).thenReturn(allottee);
        doNothing()
            .when(creatorHistoryOperationAllotteeService)
            .create(any(Allottee.class), eq(code), eq(active.operation().name()), eq(ARGS_ONE), eq(ARGS_TWO));

        executorStatusAllotteeService.adjustmentStatusAllottee(allottee, active, code, ARGS_ONE, ARGS_TWO);

        var orderExecution =
            inOrder(statusAllotteeRepository, allotteeRepository, creatorHistoryOperationAllotteeService, processingWithTopicForSendingMessage);

        orderExecution.verify(statusAllotteeRepository).findById(active.getStatusID());
        orderExecution.verify(allotteeRepository).save(argThat(entity -> entity.getStatus().getValue().equals(active.getStatusEnumName())));
        orderExecution.verify(creatorHistoryOperationAllotteeService)
            .create(any(Allottee.class), eq(code), eq(active.operation().name()), eq(ARGS_ONE), eq(ARGS_TWO));
    }

    @Test
    void shouldHavePublishExchange() {
        var allottee = new Allottee();
        var active = StatusAllotteEnum.ACTIVE;
        var code = ReasonMessages.MESSAGE_000.getCode();
        var status = new StatusAllotte();
        status.setId(active.getStatusID());
        status.setValue(active.getStatusEnumName());
        status.allowsAsynchronousProcessing(Boolean.FALSE);
        status.publish(Boolean.TRUE);

        when(statusAllotteeRepository.findById(eq(active.getStatusID()))).thenReturn(Optional.of(status));
        when(allotteeRepository.save(any(Allottee.class))).thenReturn(allottee);
        doNothing()
            .when(processingWithTopicForSendingMessage)
            .scheduleAsynchronousProcessing(eq(allottee), eq(active.operation()));
        doNothing()
            .when(creatorHistoryOperationAllotteeService)
            .create(any(Allottee.class), eq(code), eq(active.operation().name()));

        executorStatusAllotteeService.adjustmentStatusAllottee(allottee, active, code);

        verify(processingWithTopicForSendingMessage).scheduleAsynchronousProcessing(eq(allottee), eq(active.operation()));
        verify(processingMessageSendingOperationAllotte, never()).scheduleAsynchronousProcessing(eq(allottee), eq(active.operation()));
    }

    @Test
    void shouldHavePublishQueue() {
        var allottee = new Allottee();
        var active = StatusAllotteEnum.ACTIVE;
        var code = ReasonMessages.MESSAGE_000.getCode();
        var status = new StatusAllotte();
        status.setId(active.getStatusID());
        status.setValue(active.getStatusEnumName());
        status.allowsAsynchronousProcessing(Boolean.TRUE);
        status.publish(Boolean.FALSE);

        when(statusAllotteeRepository.findById(eq(active.getStatusID()))).thenReturn(Optional.of(status));
        when(allotteeRepository.save(any(Allottee.class))).thenReturn(allottee);
        doNothing()
            .when(processingMessageSendingOperationAllotte)
            .scheduleAsynchronousProcessing(eq(allottee), eq(active.operation()));
        doNothing()
            .when(creatorHistoryOperationAllotteeService)
            .create(any(Allottee.class), eq(code), eq(active.operation().name()));

        executorStatusAllotteeService.adjustmentStatusAllottee(allottee, active, code);

        verify(processingMessageSendingOperationAllotte).scheduleAsynchronousProcessing(eq(allottee), eq(active.operation()));
        verify(processingWithTopicForSendingMessage, never()).scheduleAsynchronousProcessing(eq(allottee), eq(active.operation()));
    }
}
