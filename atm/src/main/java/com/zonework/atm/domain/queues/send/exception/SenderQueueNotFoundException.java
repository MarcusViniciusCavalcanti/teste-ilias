package com.zonework.atm.domain.queues.send.exception;

import com.zonework.atm.domain.queues.configuration.properties.SenderQueueDefinition;

public class SenderQueueNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Not found dynamically defined queue %s[topic: %s, broker: %s] to send";

    public SenderQueueNotFoundException(SenderQueueDefinition queueDef) {
        super(String.format(MESSAGE_TEMPLATE, queueDef.getName(), queueDef.isTopic(), queueDef.getConnection()));
    }

}
