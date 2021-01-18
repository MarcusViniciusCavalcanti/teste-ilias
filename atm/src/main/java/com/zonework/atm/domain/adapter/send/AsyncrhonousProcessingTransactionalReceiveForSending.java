package com.zonework.atm.domain.adapter.send;

import com.zonework.atm.domain.adapter.AbstractAsynchronousProcessingSendingMessage;
import com.zonework.atm.domain.config.property.AbstractQueueProperties;
import com.zonework.atm.domain.config.property.QueueTransactionalReceiveProperties;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.struture.dto.TransactionalReceiveTriggerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncrhonousProcessingTransactionalReceiveForSending extends AbstractAsynchronousProcessingSendingMessage<TransactionalBalance> {

    private final QueueTransactionalReceiveProperties queueTransactionalReceiveProperties;

    @Override
    public AbstractQueueProperties getQueue() {
        return queueTransactionalReceiveProperties;
    }

    @Override
    public Object buildMessage(TransactionalBalance messageObject) {
        return TransactionalReceiveTriggerDTO.builder()
            .id(messageObject.getId())
            .build();
    }
}
