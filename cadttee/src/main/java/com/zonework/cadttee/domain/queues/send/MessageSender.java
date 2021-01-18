package com.zonework.cadttee.domain.queues.send;

import com.zonework.cadttee.domain.queues.configuration.properties.SenderQueueDefinition;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessages;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendingException;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendingResult;

public interface MessageSender extends BasicMessageSender {

	BatchOfMessagesSendingResult send(SenderQueueDefinition queueDefinition, BatchOfMessages batch) throws
		BatchOfMessagesSendingException;

	void sendBeforeTransactionCommit(String queue, String message);

	void sendBeforeTransactionCommit(SenderQueueDefinition queueDefinition, String message);

	void sendBeforeTransactionCommit(String queue, BatchOfMessages batch);

	void sendBeforeTransactionCommit(SenderQueueDefinition queueDefinition, BatchOfMessages batch);

}
