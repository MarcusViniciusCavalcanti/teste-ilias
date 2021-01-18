package com.zonework.atm.domain.queues.send;

import com.zonework.atm.domain.queues.configuration.properties.SenderQueueDefinition;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessages;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessagesSendingException;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessagesSendingResult;

public interface MessageSender extends BasicMessageSender {

	BatchOfMessagesSendingResult send(SenderQueueDefinition queueDefinition, BatchOfMessages batch) throws
		BatchOfMessagesSendingException;

	void sendBeforeTransactionCommit(String queue, String message);

	void sendBeforeTransactionCommit(SenderQueueDefinition queueDefinition, String message);

	void sendBeforeTransactionCommit(String queue, BatchOfMessages batch);

	void sendBeforeTransactionCommit(SenderQueueDefinition queueDefinition, BatchOfMessages batch);

}
