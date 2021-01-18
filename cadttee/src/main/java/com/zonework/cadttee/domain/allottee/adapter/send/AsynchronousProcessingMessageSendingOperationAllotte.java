package com.zonework.cadttee.domain.allottee.adapter.send;

import com.zonework.cadttee.domain.allottee.adapter.AbstractAsynchronousProcessingSendingMessage;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.config.property.AbstractQueueProperties;
import com.zonework.cadttee.domain.config.property.QueueAllotteeOperationProperties;
import com.zonework.cadttee.structure.dto.AllotteeActiveTrigger;
import com.zonework.cadttee.structure.dto.OperationEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingMessageSendingOperationAllotte extends
    AbstractAsynchronousProcessingSendingMessage<Allottee> {

    private final QueueAllotteeOperationProperties queueAllotteeProperties;

    @Override
    public AbstractQueueProperties getQueue() {
        return queueAllotteeProperties;
    }

    @Override
    public Object buildMessage(Allottee messageObject, OperationEnum operation) {
        return AllotteeActiveTrigger.builder()
            .id(messageObject.getId())
            .operation(operation)
            .value(operation.getValueByOperation(messageObject))
            .build();
    }

    @Override
    public void scheduleAsynchronousProcessing(Allottee allottee, OperationEnum operation) {
        super.scheduleAsynchronousProcessing(allottee, operation);
        log.info("Asynchronous processing of status={}, id={} prepared", allottee.getStatus().getValue(), allottee.getId());
    }
}
