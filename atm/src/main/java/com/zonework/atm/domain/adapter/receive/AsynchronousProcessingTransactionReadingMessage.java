package com.zonework.atm.domain.adapter.receive;

import com.zonework.atm.domain.adapter.MessageFormatter;
import com.zonework.atm.domain.queues.receive.listener.MessageListener;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.service.UpdatorStatusTransactional;
import com.zonework.atm.struture.dto.TransactionalDTO;
import com.zonework.atm.struture.dto.TransactionalTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("asynchronousProcessingWithQueueTransactionaForReadingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingTransactionReadingMessage implements MessageListener {

    private final MessageFormatter messageFormatter;
    private final UpdatorStatusTransactional updatorStatusTransactional;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        var dto = messageFormatter.parseMessage(message.getPayload(), TransactionalDTO.class);
        log.info("Message consumed queue: {}", message);

        if (TransactionalTypeDTO.REVERSAL == dto.getTransactionalType()) {
            updatorStatusTransactional.updateStatusBy(dto.getId(), StatusTransactionalEnum.COMPLETE);
        } else {
            updatorStatusTransactional.updateStatusBy(dto.getId(), StatusTransactionalEnum.SEND);
        }

    }

}
