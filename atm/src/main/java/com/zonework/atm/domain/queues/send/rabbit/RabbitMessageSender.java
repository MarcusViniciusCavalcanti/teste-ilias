package com.zonework.atm.domain.queues.send.rabbit;

import com.zonework.atm.domain.queues.configuration.properties.ConnectionType;
import com.zonework.atm.domain.queues.configuration.properties.MessagingProperties;
import com.zonework.atm.domain.queues.configuration.properties.SenderQueueDefinition;
import com.zonework.atm.domain.queues.configuration.rabbit.MessagingRabbitConfiguration;
import com.zonework.atm.domain.queues.send.BasicMessageSender;
import com.zonework.atm.domain.queues.send.dto.MessageToSend;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessages;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessagesSendingResult;
import com.zonework.atm.domain.queues.send.exception.SenderQueueNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

@Slf4j
public class RabbitMessageSender implements BasicMessageSender {

	@Autowired
	@Qualifier(MessagingRabbitConfiguration.RABBIT_SENDERS_CONNECTION_BEAN_ID)
	private ConnectionFactory connectionFactory;

	@Autowired
	private MessagingProperties messagingProperties;

	@Autowired(required = false)
	private AmqpAdmin amqpAdmin;

	private final Map<String, Boolean> isTopicByQueueName = new HashMap<>();

	private RabbitTemplate template;

	@PostConstruct
	public void init() {
		validateConfigs();
		template = new RabbitTemplate(connectionFactory);
		initializeIsTopicByQueue();
		declareQueues();
	}

	private void validateConfigs() {
		rabbitQueues().forEach(this::validateQueueDeclaration);
	}

	private void validateQueueDeclaration(MessagingProperties.QueueDefinition queue) {
		var delay = Integer.valueOf(0);
		if (queue.getRabbit() != null) {
			delay = queue.getRabbit().getDelay();
		}
		Assert.isTrue(!queue.isTopic() || queue.getRabbit() == null || delay.equals(0),
				String.format("Queue %s is configured as topic and also with a delay of %s, but there is no support for RabbitMQ topics with delay", queue.getName(), delay));
	}

	@Override
	public void send(String queue, String message) {
		doSend(queue, message);
	}

	private void doSend(String queue, Object object) {
		RabbitDestination destination = defineDestination(queue);

		template.convertAndSend(destination.getExchange(), destination.getRoutingKey(), object);
	}

	@Override
	public BatchOfMessagesSendingResult send(String queue, BatchOfMessages batch) {
		Message message = mountBatchOfMessages(batch);

		int size = batch.getMessagesToSend().size();

		log.debug("Mounted batch with {} messages", size);

		doSend(queue, message);

		return BatchOfMessagesSendingResult.resultWithAllSuccessful(size);
	}

	private boolean mustDelay(String queue) {
		return messagingProperties.getQueueDefinition(queue).getRabbit() != null &&
			   messagingProperties.getQueueDefinition(queue).getRabbit().getDelay() > 0;
	}

	private boolean isTopic(String queue) {
		return isTopicByQueueName.get(queue);
	}

	private void initializeIsTopicByQueue() {
		rabbitQueues()
			.forEach(queue -> {
				isTopicByQueueName.put(queue.getName(), queue.isTopic());
		});
	}

	private void declareQueues() {
		if (amqpAdmin != null ) {
			rabbitQueues()
				// TODO refatorar para predicate
				// We ignore reading queues, because they are declared in the
				//   listener declaration.
				.filter(queue -> queue.getRead() == null || queue.getRead().getListenerBeanId() == null)
				.filter(queue -> queue.getRabbit() == null || queue.getRabbit().isDeclare())
				.forEach(this::declareQueue);

			rabbitQueues()
					.filter(queue -> queue.getRabbit() != null && queue.getRabbit().isDeclare()
									 && queue.getRabbit().getDelay() > 0)
					.forEach(this::declareDelayQueue);

		} else {
			log.info("Queues for RabbitMQ will not be declared because RabbitAdmin is not present.");
		}
	}

	private void declareQueue(MessagingProperties.QueueDefinition queueDefinition) {
		var queueName = queueDefinition.getName();

		if (queueDefinition.isTopic()) {
			var exchangeBuilder = ExchangeBuilder.fanoutExchange(queueName);

			if (messagingProperties.getRabbit().isIgnoreDeclarationExceptions()) {
				exchangeBuilder.ignoreDeclarationExceptions();
			}

			var exchange = exchangeBuilder.build();

			log.info("Declaring RabbitMQ exchange: {}", exchange);
			amqpAdmin.declareExchange(exchange);

		} else {
			var queue = QueueBuilder.durable(queueName).build();
			queue.setIgnoreDeclarationExceptions(messagingProperties.getRabbit().isIgnoreDeclarationExceptions());

			log.info("Declaring RabbitMQ queue: {}", queue);
			amqpAdmin.declareQueue(queue);
		}

	}

	private void declareDelayQueue(MessagingProperties.QueueDefinition queueDefinition) {
		var queueName = queueDefinition.getName();

		var delayQueueName = mountDelayQueueName(queueName);

		var delayQueue = QueueBuilder
				.durable(delayQueueName)
				.deadLetterExchange("")
				.deadLetterRoutingKey(queueName)
				.ttl(queueDefinition.getRabbit().getDelay())
				.build();

		delayQueue.setIgnoreDeclarationExceptions(messagingProperties.getRabbit().isIgnoreDeclarationExceptions());

		log.info("Declaring RabbitMQ delay queue: {}", delayQueueName);
		amqpAdmin.declareQueue(delayQueue);
	}

	private static Message mountBatchOfMessages(BatchOfMessages batch) {
		int totalSize = 0;
		var messageConverter = new SimpleMessageConverter();

		var messages = new ArrayList<Message>();

		for (MessageToSend messageToSend : batch.getMessagesToSend()) {
			Message message = messageConverter.toMessage(messageToSend.getBody(), new MessageProperties());
			int messageSize = Integer.BYTES + message.getBody().length;
			totalSize += messageSize;
			messages.add(message);
		}

		byte[] body = new byte[totalSize];

		ByteBuffer bytes = ByteBuffer.wrap(body);
		for (Message message : messages) {
			bytes.putInt(message.getBody().length);
			bytes.put(message.getBody());
		}
		var messageProperties = new MessageProperties();
		messageProperties.getHeaders().put(MessageProperties.SPRING_BATCH_FORMAT,
				MessageProperties.BATCH_FORMAT_LENGTH_HEADER4);
		messageProperties.getHeaders().put(AmqpHeaders.BATCH_SIZE, messages.size());

		return new Message(body, messageProperties);
	}

	private Stream<MessagingProperties.QueueDefinition> rabbitQueues() {
		return 	messagingProperties.getQueues().stream()
				.filter(q -> q.getName() != null)
				.filter(q -> ConnectionType.RABBIT == q.getConnection());
	}

	private String mountDelayQueueName(String originalQueueName) {
		return originalQueueName + messagingProperties.getRabbit().getDelayQueuesSuffix();
	}

	@Override
	public void initDynamicallySpecifiedQueue(SenderQueueDefinition senderQueueDefinition) {
		if (queueDoesNotExists(senderQueueDefinition)) {
			throw new SenderQueueNotFoundException(senderQueueDefinition);
		}
		isTopicByQueueName.put(senderQueueDefinition.getName(), senderQueueDefinition.isTopic());
	}

	private boolean queueDoesNotExists(SenderQueueDefinition senderQueueDefinition) {
		var declarePassiveReturn = template.execute(channel -> {
			try {
				if (senderQueueDefinition.isTopic()) {
					return channel.exchangeDeclarePassive(senderQueueDefinition.getName());
				}

				return channel.queueDeclarePassive(senderQueueDefinition.getName());
			} catch (Exception e) {
				log.debug("Verifying if queue exists using declarePassive returned exception:", e);
				log.info("{}[topic: {}] does not exists", senderQueueDefinition.getName(),
					senderQueueDefinition.isTopic());
				return null;
			}
		});

		return declarePassiveReturn == null;
	}

	private RabbitDestination defineDestination(String queue) {
		var exchange = "";
		var routingKey = queue;

		if (mustDelay(queue)) {
			routingKey = mountDelayQueueName(queue);
		} else if (isTopic(queue)) {
			exchange = queue;
			routingKey = "";
		}

		return new RabbitDestination(exchange, routingKey);
	}

	@AllArgsConstructor
	@Getter
	@Setter
	static class RabbitDestination {
		private String exchange;
		private String routingKey;
	}

}
