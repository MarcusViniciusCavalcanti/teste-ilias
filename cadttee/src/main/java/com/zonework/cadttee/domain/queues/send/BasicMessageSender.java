package com.zonework.cadttee.domain.queues.send;

import com.zonework.cadttee.domain.queues.configuration.properties.SenderQueueDefinition;
import com.zonework.cadttee.domain.queues.send.dto.MessageToSend;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessages;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendingException;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendingResult;

// TODO refatorar
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
