package com.zonework.atm.domain.adapter.send;

import com.zonework.atm.domain.adapter.AbstractAsynchronousProcessingSendingMessage;
import com.zonework.atm.domain.config.property.AbstractQueueProperties;
import com.zonework.atm.domain.config.property.QueueTransactionalSendProperties;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.struture.dto.TransactionalDTO;
import com.zonework.atm.struture.dto.TransactionalTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingWithTopicForSendingMessage extends
    AbstractAsynchronousProcessingSendingMessage<TransactionalBalance> {

    private final QueueTransactionalSendProperties queueTransactionalBalanceProperty;

    @Override
    public AbstractQueueProperties getQueue() {
        return queueTransactionalBalanceProperty;
    }

    @Override
    public Object buildMessage(TransactionalBalance messageObject) {
        return TransactionalDTO.builder()
            .value(messageObject.getAllottee().getRetirementBalance().doubleValue())
            .id(messageObject.getId())
            .idAllottee(messageObject.getAllottee().getId())
            .transactionalType(TransactionalTypeDTO.valueOf(messageObject.getType().name()))
            .build();
    }

}
