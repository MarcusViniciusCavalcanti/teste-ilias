package com.zonework.atm.domain.queues.receive.error.handler;

import org.springframework.messaging.Message;

@FunctionalInterface
public interface MessageErrorHandler {

	void handleError(Message<String> message, String originalQueueName, String dlqName, Exception ex);

}
