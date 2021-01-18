package com.zonework.atm.domain.queues.receive.listener;

import org.springframework.messaging.Message;

@FunctionalInterface
public interface MessageListener {

	void onMessage(Message<String> message);
}
