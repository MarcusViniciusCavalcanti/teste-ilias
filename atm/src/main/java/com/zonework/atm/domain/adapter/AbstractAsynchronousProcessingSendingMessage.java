package com.zonework.atm.domain.adapter;

import com.zonework.atm.domain.config.property.AbstractQueueProperties;
import com.zonework.atm.domain.queues.send.MessageSender;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter(onMethod = @__(@Autowired))
public abstract class AbstractAsynchronousProcessingSendingMessage<M> {
    private MessageFormatter messageFormatter;
    private MessageSender messageSender;

    public abstract AbstractQueueProperties getQueue();

    public abstract Object buildMessage(M messageObject);

    public void scheduleAsynchronousProcessing(M messageObject) {
        var message = messageFormatter.formatMessage(buildMessage(messageObject));
        var queueName = getQueue().getName();

        messageSender.sendBeforeTransactionCommit(queueName, message);
    }

}
