package com.zonework.cadttee.domain.allottee.adapter.send;

import com.zonework.cadttee.domain.allottee.adapter.AbstractAsynchronousProcessingSendingMessage;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.factory.AllotteeFactory;
import com.zonework.cadttee.domain.config.property.AbstractQueueProperties;
import com.zonework.cadttee.domain.config.property.QueueAllotteeProperties;
import com.zonework.cadttee.structure.dto.MessageOperationAllotteTriggerDTO;
import com.zonework.cadttee.structure.dto.OperationEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingWithTopicForSendingMessage extends
    AbstractAsynchronousProcessingSendingMessage<Allottee> {

    private final QueueAllotteeProperties queueAllotteeProperties;

    @Override
    public AbstractQueueProperties getQueue() {
        return queueAllotteeProperties;
    }

    @Override
    public Object buildMessage(Allottee messageObject, OperationEnum operation) {
        var allotteDTO = AllotteeFactory.buildEntityByAllottee(messageObject);
        return MessageOperationAllotteTriggerDTO.builder()
            .allotteeDTO(allotteDTO)
            .operation(operation)
            .build();
    }

}
