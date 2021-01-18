package com.zonework.atm.domain.adapter.receive;

import com.zonework.atm.domain.adapter.MessageFormatter;
import com.zonework.atm.domain.allottee.service.AllotteService;
import com.zonework.atm.domain.queues.receive.listener.MessageListener;
import com.zonework.atm.struture.dto.MessageOperationAllotteTriggerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("asynchronousProcessingRegistrationAllotteeForReadingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingRegistrationAllotteeForReadingMessage implements MessageListener {

    private final MessageFormatter messageFormatter;
    private final AllotteService allotteService;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        var activeTrigger = messageFormatter
            .parseMessage(message.getPayload(), MessageOperationAllotteTriggerDTO.class);

        log.info("Message consumed from queue: {}", message.getPayload());

        var allotteeDTO = activeTrigger.getAllotteeDTO();

        allotteService.create(allotteeDTO);
    }
}
