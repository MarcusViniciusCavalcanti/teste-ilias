package com.zonework.cadttee.domain.allottee.adapter.receive;

import com.zonework.cadttee.domain.allottee.adapter.MessageFormatter;
import com.zonework.cadttee.domain.allottee.service.UpdatorRetirementBalance;
import com.zonework.cadttee.domain.queues.receive.listener.MessageListener;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("asynchronousProcessingUpateRetimenetForReadingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingUpateRetimenetForReadingMessage implements MessageListener {
    private final MessageFormatter messageFormatter;
    private final UpdatorRetirementBalance updatorRetirementBalance;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        var transactional = messageFormatter.parseMessage(message.getPayload(), TransactionalDTO.class);

        log.info("Consumed queue {}", message);
        updatorRetirementBalance.executeUpdateRetirement(transactional);
    }
}
