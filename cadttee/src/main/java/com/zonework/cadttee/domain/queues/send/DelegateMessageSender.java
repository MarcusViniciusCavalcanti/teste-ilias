package com.zonework.cadttee.domain.queues.send;

import com.zonework.cadttee.domain.queues.configuration.properties.ConnectionType;
import com.zonework.cadttee.domain.queues.configuration.properties.MessagingProperties;
import com.zonework.cadttee.domain.queues.configuration.properties.PropertiesNames;
import com.zonework.cadttee.domain.queues.configuration.properties.SenderQueueDefinition;
import com.zonework.cadttee.domain.queues.send.dto.MessageSendRequestDTO;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessages;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendRequestDTO;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendingException;
import com.zonework.cadttee.domain.queues.send.dto.batch.BatchOfMessagesSendingResult;
import com.zonework.cadttee.domain.queues.send.rabbit.RabbitMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Primary
public class DelegateMessageSender implements MessageSender {


	@Autowired(required = false)
	private RabbitMessageSender rabbitMessageSender;

	private Map<String, BasicMessageSender> senders = new HashMap<>();

	@Autowired
	private MessagingProperties messagingProperties;

	@Autowired
	private ApplicationContext context;

	@PostConstruct
	public void init() {
		messagingProperties.getQueues()
			.forEach(queue -> senders.put(queue.getName(), getActualSender(queue.getConnection())));
	}

	public void send(String queue, String message) {
		BasicMessageSender actualSender = getActualSender(queue);
		actualSender.send(queue, message);
		log.debug("Message {} sent to {} {}", message, queueType(queue), queue);
	}

	private BasicMessageSender getActualSender(ConnectionType connectionType) {
		if (ConnectionType.RABBIT == connectionType) {
			Assert.notNull(rabbitMessageSender,
					String.format("RabbitMessageSender not instantiaded. Make sure that the property %s is not set to false.",
							PropertiesNames.RABBIT_ACTIVATION));
			return rabbitMessageSender;
		}
		throw new IllegalStateException(String.format("There is no MessageSender for connectionType %s", connectionType));
	}

	private Object queueType(String queue) {
		return messagingProperties.getQueueDefinition(queue).getQueueType();
	}

	@Override
	public void sendBeforeTransactionCommit(String queue, String message) {
		assertThereIsCurrentTransactionActive();
		context.publishEvent(MessageSendRequestDTO.builder()
				.queue(queue)
				.message(message)
				.build());
	}

	@Override
	public void sendBeforeTransactionCommit(SenderQueueDefinition queueDefinition, String message) {
		initializeQueueIfNeeded(queueDefinition);
		sendBeforeTransactionCommit(queueDefinition.getName(), message);
	}

	@Override
	public void initDynamicallySpecifiedQueue(SenderQueueDefinition senderQueueDefinition) {
		BasicMessageSender actualSender = getActualSender(senderQueueDefinition.getConnection());
		actualSender.initDynamicallySpecifiedQueue(senderQueueDefinition);
		addSenderQueueAtProperties(senderQueueDefinition);
		senders.put(senderQueueDefinition.getName(), actualSender);
	}

	private void addSenderQueueAtProperties(SenderQueueDefinition queueDefinition) {
		var properties = new MessagingProperties.QueueDefinition();
		properties.setName(queueDefinition.getName());
		properties.setConnection(queueDefinition.getConnection());
		properties.setTopic(queueDefinition.isTopic());

		messagingProperties.getQueues().add(properties);
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void handleSendEvent(MessageSendRequestDTO sendRequest) {
		log.debug("Actually sending message to queue {} before commit.", sendRequest.getQueue());
		send(sendRequest.getQueue(), sendRequest.getMessage());
	}

	@Override
	public BatchOfMessagesSendingResult send(String queue, BatchOfMessages batch) throws BatchOfMessagesSendingException {
		var actualSender = getActualSender(queue);
		var result = actualSender.send(queue, batch);
		log.debug("Batch of messages sent to queue {}. Result: {}", queue, result);

		return result;
	}

	@Override
	public BatchOfMessagesSendingResult send(SenderQueueDefinition queueDefinition, BatchOfMessages batch) throws BatchOfMessagesSendingException {
		initializeQueueIfNeeded(queueDefinition);
		return send(queueDefinition.getName(), batch);
	}

	@Override
	public void sendBeforeTransactionCommit(String queue, BatchOfMessages batch) {
		assertThereIsCurrentTransactionActive();
		context.publishEvent(BatchOfMessagesSendRequestDTO.builder()
				.batchOfMessages(batch)
				.queue(queue)
				.build());
	}

	@Override
	public void sendBeforeTransactionCommit(SenderQueueDefinition queueDefinition, BatchOfMessages batch) {
		initializeQueueIfNeeded(queueDefinition);
		sendBeforeTransactionCommit(queueDefinition.getName(), batch);
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void handleBatchSendEvent(BatchOfMessagesSendRequestDTO sendRequest) {
		log.debug("Actually sending message batch to queue {} before commit.", sendRequest.getQueue());
		BatchOfMessagesSendingResult result;
		try {
			result = send(sendRequest.getQueue(), sendRequest.getBatchOfMessages());
		} catch (BatchOfMessagesSendingException e) {
			result = e.getResult();
		}
		if (!result.isAllMessagesWereSuccesfull()) {
			throw new RuntimeException(String.format("Error sendind batch of messages before transaction commit. Result: %s", result));
		}
	}

	private BasicMessageSender getActualSender(String queue) {
		BasicMessageSender actualSender = senders.get(queue);
		Assert.notNull(actualSender, String.format("There is no messageSender for queue %s. "
				+ "Assure that this queue is configured in the property application.messages.queues", queue));
		return actualSender;
	}

	private void initializeQueueIfNeeded(SenderQueueDefinition queueDefinition) {
		String queueName = queueDefinition.getName();
		BasicMessageSender actualSender = senders.get(queueName);
		if (actualSender == null) {
			initDynamicallySpecifiedQueue(queueDefinition);
		}
	}

	private static void assertThereIsCurrentTransactionActive() {
		Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "sendBeforeTransactionCommit() was called but there is no transaction active.");
	}

}
