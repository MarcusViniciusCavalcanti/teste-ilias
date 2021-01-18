package com.zonework.atm.domain.adapter.receive;

import com.zonework.atm.domain.adapter.MessageFormatter;
import com.zonework.atm.domain.queues.receive.listener.MessageListener;
import com.zonework.atm.domain.transactional.service.ExecutorUpdateRetirementBalance;
import com.zonework.atm.struture.dto.TransactionalReceiveTriggerDTO;
import com.zonework.atm.struture.dto.TransactionalTypeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("asynchronousProcessingTransactionalReceiveForReadingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingTransactionalReceiveForReadingMessage implements MessageListener {

    private final MessageFormatter messageFormatter;
    private final ExecutorUpdateRetirementBalance updateRetirementBalance;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        log.info("consumed to queue {}", message);
        var trigger = messageFormatter.parseMessage(message.getPayload(), TransactionalReceiveTriggerDTO.class);

        updateRetirementBalance.execute(trigger.getId());
    }

}
