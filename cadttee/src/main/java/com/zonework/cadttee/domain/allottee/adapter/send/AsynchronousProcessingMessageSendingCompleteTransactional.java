package com.zonework.cadttee.domain.allottee.adapter.send;

import com.zonework.cadttee.domain.allottee.adapter.AbstractAsynchronousProcessingSendingMessage;
import com.zonework.cadttee.domain.config.property.AbstractQueueProperties;
import com.zonework.cadttee.domain.config.property.QueueCompleteTransactionalProperties;
import com.zonework.cadttee.structure.dto.CompleteTransactionalTriggerDTO;
import com.zonework.cadttee.structure.dto.OperationEnum;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingMessageSendingCompleteTransactional extends
    AbstractAsynchronousProcessingSendingMessage<TransactionalDTO> {

    private final QueueCompleteTransactionalProperties queueAllotteeProperties;

    @Override
    public AbstractQueueProperties getQueue() {
        return queueAllotteeProperties;
    }

    @Override
    public Object buildMessage(TransactionalDTO messageObject, OperationEnum operation) {
        return CompleteTransactionalTriggerDTO.builder()
            .idTransactional(messageObject.getId())
            .operation(operation)
            .build();
    }

    @Override
    public void scheduleAsynchronousProcessing(TransactionalDTO transactional, OperationEnum operation) {
        super.scheduleAsynchronousProcessing(transactional, operation);
        log.info("Asynchronous processing of transactional by id: {} prepared", transactional.getId());
    }
}
