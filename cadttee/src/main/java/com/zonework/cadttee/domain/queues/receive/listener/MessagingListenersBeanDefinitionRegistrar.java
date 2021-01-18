package com.zonework.cadttee.domain.queues.receive.listener;


import com.zonework.cadttee.domain.queues.configuration.properties.ConnectionType;
import com.zonework.cadttee.domain.queues.configuration.properties.PropertiesNames;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.configuration.RabbitListenerBeanDefinitionRegistrar;
import com.zonework.cadttee.domain.queues.util.environment.EnvironmentArrayPropertiesReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
public class MessagingListenersBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		EnvironmentArrayPropertiesReader
			queuesReader = new EnvironmentArrayPropertiesReader(environment, PropertiesNames.QUEUES, PropertiesNames.QUEUE_NAME);

		while (queuesReader.hasNext()) {

			if (queuesReader.getProperty(PropertiesNames.SIMPLE_LISTENER_BEAN_ID) != null) {

				ConnectionType connectionType = getConnectionType(environment, queuesReader);

				assertConnectionTypeSupportsListeners(connectionType, queuesReader);

				Optional<AbstractListenerBeanDefinitionRegistrar> listenerRegistrar =
						getListenerRegistrar(connectionType, queuesReader, registry);

				if (listenerRegistrar.isPresent()) {
					listenerRegistrar.get().registerQueueListener();
				}
			} else {
				log.debug("Not configuring listener for queue {}", queuesReader.getProperty(PropertiesNames.QUEUE_NAME));
			}

			queuesReader.advance();
		}
	}

	private static void assertConnectionTypeSupportsListeners(ConnectionType connectionType,
															  EnvironmentArrayPropertiesReader queuesReader) {
		Assert.isTrue(connectionType.isSuportsListening(),
				String.format("For the queue %s was defined a listener, but its connection type (%s) "
						+ "does not support listeners.", queuesReader.getProperty(PropertiesNames.QUEUE_NAME), connectionType));
	}

	private static ConnectionType getConnectionType(Environment environment,
													EnvironmentArrayPropertiesReader queuesReader) {
		ConnectionType connectionType = queuesReader.getRequiredProperty(PropertiesNames.QUEUE_CONNECTION_TYPE, ConnectionType.class);

		validateConfigs(connectionType, environment);

		return connectionType;
	}

	private static void validateConfigs(ConnectionType connectionType, Environment env) {
		String connectionTypeActivation = env.getProperty(connectionType.getActivationProperty());
		Assert.state(connectionTypeActivation == null || Boolean.parseBoolean(connectionTypeActivation),
				String.format("At least one queue was defined to be from %s but %s configuration was disabled.", connectionType, connectionType));
	}

	private Optional<AbstractListenerBeanDefinitionRegistrar> getListenerRegistrar(ConnectionType connectionType,
			EnvironmentArrayPropertiesReader queuesReader, BeanDefinitionRegistry registry) {

		if (ConnectionType.RABBIT == connectionType) {
			return Optional.of(new RabbitListenerBeanDefinitionRegistrar(queuesReader, registry, environment));
		}
		return Optional.empty();
	}

}
