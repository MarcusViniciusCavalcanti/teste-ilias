package com.zonework.cadttee.domain.allottee.adapter;


import com.zonework.cadttee.domain.config.property.AbstractQueueProperties;
import com.zonework.cadttee.domain.queues.send.MessageSender;
import com.zonework.cadttee.structure.dto.OperationEnum;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Setter(onMethod = @__(@Autowired))
public abstract class AbstractAsynchronousProcessingSendingMessage<M> {

    private MessageFormatter messageFormatter;
    private MessageSender messageSender;

    public abstract AbstractQueueProperties getQueue();

    public abstract Object buildMessage(M messageObject, OperationEnum operation);

    public void scheduleAsynchronousProcessing(M messageObject, OperationEnum operation) {
        var message = messageFormatter.formatMessage(buildMessage(messageObject, operation));
        var queueName = getQueue().getName();

        messageSender.sendBeforeTransactionCommit(queueName, message);
    }
}
