package com.zonework.cadttee.domain.queues.receive.listener;


import com.zonework.cadttee.domain.queues.configuration.properties.ConnectionType;
import com.zonework.cadttee.domain.queues.configuration.properties.MessagingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@Slf4j
public class DelegateListenerManager implements ListenerManager {

	private final Map<String, ListenerManager> managers = new HashMap<>();

	@Autowired
	private ApplicationContext context;

	@Autowired
	private MessagingProperties messagingProperties;

	@PostConstruct
	public void init() {
		messagingProperties.getQueues()
			.stream()
			.filter(q -> q.getRead() != null)
			.filter(q -> q.getRead().getListenerBeanId() != null)
			.forEach(queue -> {
				managers.put(queue.getName(), getActualManagerOfConnectionType(queue.getConnection(), queue.getName()));
			});
	}

	public void stop(String queue) {
		ListenerManager actualManager = getActualManager(queue);
		actualManager.stop(queue);
		log.info("Listening of messages from queue {} stopped.", queue);
	}

	public void start(String queue) {
		ListenerManager actualManager = getActualManager(queue);
		actualManager.start(queue);
		log.info("Listening of messages from queue {} started.", queue);
	}

	private ListenerManager getActualManager(String queue) {
		ListenerManager actualManager = managers.get(queue);
		Assert.notNull(actualManager, String.format("There is no ListenerManager for queue %s. "
				+ "Assure that this queue is configured in the property application.messages.queues", queue));
		return actualManager;
	}

	private ListenerManager getActualManagerOfConnectionType(ConnectionType connectionType, String queue) {
		if (!connectionType.isSuportsListening()) {
			throw new UnsupportedOperationException(String.format("Queue %s was configured with a message listener, but the ConnectionType %s does not support listeners.", queue, connectionType));
		}

		try {
			String beanId = ListenerManager.listenerManagerBeanId(connectionType);
			return context.getBean(beanId, ListenerManager.class);
		} catch (NoSuchBeanDefinitionException ex) {
			throw new IllegalStateException(
					String.format("ListenerManager for %s not found. Make sure that the property %s is not set to false.",
							connectionType, connectionType.getActivationProperty()));
		}
	}

}
