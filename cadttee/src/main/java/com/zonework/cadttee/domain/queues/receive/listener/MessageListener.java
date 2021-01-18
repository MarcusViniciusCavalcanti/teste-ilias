package com.zonework.cadttee.domain.queues.receive.listener;

import org.springframework.messaging.Message;

public interface MessageListener {

	void onMessage(Message<String> message);
}
