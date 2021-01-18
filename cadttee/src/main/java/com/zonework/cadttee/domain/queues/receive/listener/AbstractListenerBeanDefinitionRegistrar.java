package com.zonework.cadttee.domain.queues.receive.listener;


import com.zonework.cadttee.domain.queues.configuration.properties.ConnectionType;
import com.zonework.cadttee.domain.queues.configuration.properties.PropertiesNames;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.config.GenericMessageErrorHandlerConfiguration;
import com.zonework.cadttee.domain.queues.util.environment.EnvironmentArrayPropertiesReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.StringUtils;

import java.util.Locale;

@AllArgsConstructor
@Slf4j
public abstract class AbstractListenerBeanDefinitionRegistrar {

	private final EnvironmentArrayPropertiesReader queuePropertiesReader;

	private final BeanDefinitionRegistry registry;

	private final ConnectionType connectionType;

	public void registerQueueListener() {
		var queueName = getQueueName(queuePropertiesReader);
		var simpleListenerBeanId = queuePropertiesReader.getRequiredProperty(PropertiesNames.SIMPLE_LISTENER_BEAN_ID);

		registerMainListener(queueName, simpleListenerBeanId);

		if (connectionType.isSupportsDlq()) {
			var dlqName = queuePropertiesReader.getProperty(PropertiesNames.DLQ_NAME);

			if (dlqName != null) {
				registerDlqListener(queueName, dlqName, simpleListenerBeanId);
			}
		}
	}

	private void registerMainListener(String queueName, String simpleListenerBeanId) {
		log.info("Configuring {} listener for queue {} with listener {}", connectionType, queueName, simpleListenerBeanId);

		var specificListenerType = createSpecificListenerType(queueName, queuePropertiesReader);

		registerListenerBean(specificListenerType, simpleListenerBeanId, queueName);
	}

	private void registerDlqListener(String queueName, String dlqName, String originalListenerBeanId) {

		var errorHandlerBeanId = queuePropertiesReader.getProperty(PropertiesNames.DLQ_ERROR_HANDLER);

		if (!StringUtils.hasText(errorHandlerBeanId)) {
			errorHandlerBeanId = GenericMessageErrorHandlerConfiguration.GENERIC_MESSAGE_ERROR_HANDLER_BEAN_ID;
			log.debug("DLQ listener of queue {} will be handled by the generic message error handler.", queueName);
		}

		BeanDefinition errorHandlerMessageListenerBeanDefinition = BeanDefinitionBuilder
				.genericBeanDefinition(ErrorHandlerMessageListener.class)
				.addConstructorArgReference(originalListenerBeanId)
				.addConstructorArgReference(errorHandlerBeanId)
				.addConstructorArgValue(queueName)
				.addConstructorArgValue(dlqName)
				.getBeanDefinition();

		var errorHandlerMessageListenerBeanId = "errorHandler" + dlqName;

		registry.registerBeanDefinition(errorHandlerMessageListenerBeanId, errorHandlerMessageListenerBeanDefinition);

		var dlqSpecificListenerType = createSpecificDlqListenerType(dlqName, queuePropertiesReader);

		log.info("Configuring {} listener for dead-letter-queue {}", connectionType, dlqName);

		registerListenerBean(dlqSpecificListenerType, errorHandlerMessageListenerBeanId, dlqName);
	}

	protected abstract Class<?> createSpecificDlqListenerType(String dlqName,
															  EnvironmentArrayPropertiesReader queuePropertiesReader2);

	protected abstract Class<?> createSpecificListenerType(String queueName,
														   EnvironmentArrayPropertiesReader arrayPropertiesReader);

	private void registerListenerBean(Class<?> specificListenerType,
									  String messageListenerBeanId,
									  String queueName) {

		var listenerBeanDefinition = BeanDefinitionBuilder
				.genericBeanDefinition(specificListenerType)
				.addConstructorArgReference(messageListenerBeanId)
				.addConstructorArgValue(queueName)
				.getBeanDefinition();

		var listenerBeanName = connectionType.name().toLowerCase(Locale.ENGLISH) + "Listener" + queueName;
		registry.registerBeanDefinition(listenerBeanName, listenerBeanDefinition);
	}

	protected String getQueueName(EnvironmentArrayPropertiesReader propertiesReader) {
		return propertiesReader.getRequiredProperty(PropertiesNames.QUEUE_NAME);
	}

}
