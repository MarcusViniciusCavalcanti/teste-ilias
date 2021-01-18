package com.zonework.cadttee.domain.queues.receive.listener;

import com.zonework.cadttee.domain.queues.receive.error.handler.MessageErrorHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

@Slf4j
@AllArgsConstructor
public class ErrorHandlerMessageListener implements MessageListener {

	private final MessageListener originalSimpleMessageListener;

	private final MessageErrorHandler errorHandler;

	private final String originalQueueName;

	private final String dlqQueueName;

	@Override
	public void onMessage(Message<String> message) {
		try {
			originalSimpleMessageListener.onMessage(message);
			log.info("Message processing succeeded from the DLQ {}", dlqQueueName);
		} catch (Exception ex) {
			log.info("Handling message with error read from DLQ {}", dlqQueueName);
			try {
				errorHandler.handleError(message, originalQueueName, dlqQueueName, ex);
			} catch (Exception ex2) {
				log.error("Error handling error from DLQ", ex2);
				throw ex2;
			}
		}

	}

}
