package com.zonework.cadttee.domain.queues.receive.listener.rabbit;

import com.zonework.cadttee.domain.queues.receive.listener.ListenerManager;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class RabbitListenerManager implements ListenerManager {

	@Autowired
	private RabbitListenerEndpointRegistry registry;

	@Override
	public void stop(String queue) {
		registry.getListenerContainer(RabbitListenerUtils.listenerId(queue)).stop();
	}

	@Override
	public void start(String queue) {
		registry.getListenerContainer(RabbitListenerUtils.listenerId(queue)).start();
	}

}
