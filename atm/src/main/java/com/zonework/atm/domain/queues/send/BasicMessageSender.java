package com.zonework.atm.domain.queues.send;

import com.zonework.atm.domain.queues.configuration.properties.SenderQueueDefinition;
import com.zonework.atm.domain.queues.send.dto.MessageToSend;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessages;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessagesSendingException;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessagesSendingResult;

@FunctionalInterface
public interface BasicMessageSender {

	void send(String queue, String message);

	default BatchOfMessagesSendingResult send(String queue, BatchOfMessages batch) throws
		BatchOfMessagesSendingException {
		for (MessageToSend messageToSend : batch.getMessagesToSend()) {
			send(queue, messageToSend.getBody());
		}
		return BatchOfMessagesSendingResult.resultWithAllSuccessful(batch.getMessagesToSend().size());
	}

	default void initDynamicallySpecifiedQueue(SenderQueueDefinition senderQueueDefinition) {/*empty implementation*/}
}
