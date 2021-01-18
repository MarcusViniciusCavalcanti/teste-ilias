package com.zonework.cadttee.domain.queues.receive.listener.rabbit;

import com.zonework.cadttee.domain.queues.configuration.properties.MessagingProperties;
import com.zonework.cadttee.domain.queues.receive.error.handler.MessageErrorHandler;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.MessageErrorHandlerSpecification;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.config.GenericMessageErrorHandlerConfiguration;
import com.zonework.cadttee.domain.queues.receive.listener.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

@Slf4j
public class BaseRabbitListener {

	public static final String METHOD_NAME = "onReceivedMessage";

	private final MessageListener listener;

	private final String queueName;

	private MessageErrorHandler errorHandler;

	public BaseRabbitListener(MessageListener listener, String queueName) {
		this.listener = listener;
		this.queueName = queueName;
	}

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private MessagingProperties messagingProperties;

	@Autowired
	private ApplicationContext context;

	@PostConstruct
	public void init() {
		declareRetryQueue();
		defineErrorHandler();
	}

	private void declareRetryQueue() {
		var retryQueue = QueueBuilder
			.durable(queueName + messagingProperties.getRabbit().getRetryQueuesSuffix())
			.deadLetterExchange("")
			.deadLetterRoutingKey(queueName)
			.ttl(getReadRabbitProperties().getRetryInterval())
			.build();

		retryQueue.setIgnoreDeclarationExceptions(messagingProperties.getRabbit().isIgnoreDeclarationExceptions());

		log.info("Declaring retry queue for queue {}: {}", queueName, retryQueue);

		amqpAdmin.declareQueue(retryQueue);
	}

	public void onReceivedMessage(Message<String> message, org.springframework.amqp.core.Message amqpMessage) {
		log.debug("Received message: {}", message.getPayload());

		try {
			listener.onMessage(message);
		} catch (Exception ex) {
			if (exceededRetries(amqpMessage)) {
				log.info("Invoking error handler for RabbitMQ message due to exceeed amount of retries.");
				errorHandler.handleError(message, queueName, getRetryQueueName(), ex);
			} else {
				throw ex;
			}
		}
	}

	private boolean exceededRetries(org.springframework.amqp.core.Message amqpMessage) {
        List<Map<String, ?>> xDeathHeader = amqpMessage.getMessageProperties().getXDeathHeader();
        if (xDeathHeader != null && xDeathHeader.size() >= 1) {
			log.debug("xDeathHeader: {}", xDeathHeader);
            Long count = (Long) xDeathHeader.get(0).get("count");
            return count != null && count >= getMaximumRetries();
        }
		return false;
	}

	private String getRetryQueueName() {
		return queueName + messagingProperties.getRabbit().getRetryQueuesSuffix();
	}

	private Integer getMaximumRetries() {
		return messagingProperties.getQueueDefinition(queueName).getRead().getRabbit().getMaxRetries();
	}

	private void defineErrorHandler() {
		var errorHandlerBeanId = messagingProperties.getQueueDefinition(queueName).getRead().getDlq().getErrorHandlerBeanId();
		if (!StringUtils.hasLength(errorHandlerBeanId)) {
			errorHandlerBeanId = GenericMessageErrorHandlerConfiguration.GENERIC_MESSAGE_ERROR_HANDLER_BEAN_ID;
		}

		try {
			errorHandler = context.getBean(errorHandlerBeanId, MessageErrorHandler.class);
		} catch (NoSuchBeanDefinitionException ex) {
			var usedGenericHandler = true;
			var errorMessage = usedGenericHandler ?
					String.format("For queue %s it was not defined an errorHandler, but the generic error handler was not found in the context. To use the generic error handler, provide a bean of type %s.", queueName, MessageErrorHandlerSpecification.class) :
					String.format("For queue %s was defined the errorHandler %s, but there is now bean in the context.", queueName, errorHandlerBeanId);
			throw new IllegalStateException(errorMessage);
		}
	}

	private MessagingProperties.ReadRabbitProperties getReadRabbitProperties() {
		return messagingProperties.getQueueDefinition(queueName).getRead().getRabbit();
	}

}
