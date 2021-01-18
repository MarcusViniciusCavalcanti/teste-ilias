package com.zonework.cadttee.domain.queues.receive.error.handler;

import org.springframework.messaging.Message;

public interface MessageErrorHandler {

	void handleError(Message<String> message, String originalQueueName, String dlqName, Exception ex);

}
