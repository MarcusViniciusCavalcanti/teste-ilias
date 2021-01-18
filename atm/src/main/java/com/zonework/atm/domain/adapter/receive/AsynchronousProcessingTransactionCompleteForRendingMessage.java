package com.zonework.atm.domain.adapter.receive;

import com.zonework.atm.domain.adapter.MessageFormatter;
import com.zonework.atm.domain.queues.receive.listener.MessageListener;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.service.UpdatorStatusTransactional;
import com.zonework.atm.struture.dto.CompleteTransactionalTriggerDTO;
import com.zonework.atm.struture.dto.TransactionalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("asynchronousProcessingTransactionCompleteForRendingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingTransactionCompleteForRendingMessage implements MessageListener {

    private final MessageFormatter messageFormatter;
    private final UpdatorStatusTransactional updatorStatusTransactional;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        var dto = messageFormatter.parseMessage(message.getPayload(), CompleteTransactionalTriggerDTO.class);
        log.info("Message consumed queue: {}", message);

        updatorStatusTransactional.updateStatusBy(dto.getIdTransactional(), StatusTransactionalEnum.COMPLETE);
    }

}
