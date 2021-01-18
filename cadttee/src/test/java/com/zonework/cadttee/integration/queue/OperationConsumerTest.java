package com.zonework.cadttee.integration.queue;

import com.zonework.cadttee.domain.allottee.adapter.MessageFormatter;
import com.zonework.cadttee.domain.queues.receive.listener.MessageListener;
import com.zonework.cadttee.structure.dto.AllotteeActiveTrigger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("operationConsumerTest")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationConsumerTest implements MessageListener {
    private final MessageFormatter messageFormatter;

    @Getter
    private boolean receiveMessage;

    @Getter
    private AllotteeActiveTrigger message;

    @Override
    public void onMessage(Message<String> message) {
        this.message = messageFormatter
            .parseMessage(message.getPayload(), AllotteeActiveTrigger.class);

        receiveMessage = true;
    }

}
